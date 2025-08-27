const API_BASE = "http://localhost:8080";

// Utilitário fetch com tratamento de erro
async function fetchJSON(endpoint) {
  try {
    const res = await fetch(endpoint);
    if (!res.ok) throw new Error(`Erro ${res.status} ao buscar ${endpoint}`);
    return await res.json();
  } catch (err) {
    console.error("Erro em fetchJSON:", err);
    return [];
  }
}

// Atualiza os cards do topo (saldo, lucros, gastos) - VERSÃO CORRIGIDA
function updateCards(saldo, lucros, gastos, prevSaldo) {
  const saldoCard = document.querySelector("#saldo-card .card-value");
  const saldoTrend = document.querySelector("#saldo-card .card-trend");
  const lucrosCard = document.querySelector("#lucros-card .card-value");
  const gastosCard = document.querySelector("#gastos-card .card-value");

  if (saldoCard) saldoCard.textContent = `R$ ${saldo.toFixed(2)}`;
  if (lucrosCard) lucrosCard.textContent = `R$ ${lucros.toFixed(2)}`;
  if (gastosCard) gastosCard.textContent = `R$ ${gastos.toFixed(2)}`;

  if (prevSaldo != null) {
    // *** A CORREÇÃO ESTÁ AQUI ***
    // Usamos Math.abs(prevSaldo) no denominador para garantir que a variação percentual
    // funcione corretamente com números negativos.
    // Usamos `|| 1` para evitar divisão por zero se o saldo anterior for 0.
    const diff = ((saldo - prevSaldo) / (Math.abs(prevSaldo) || 1)) * 100;
    
    if (saldoTrend) {
      // A lógica para cor e sinal agora funciona como esperado:
      // Se o saldo aumentou (ex: -4000 para -3000), diff será positivo.
      // Se o saldo diminuiu (ex: -3000 para -4000), diff será negativo.
      saldoTrend.textContent = `${diff >= 0 ? "+" : ""}${diff.toFixed(1)}% este mês`;
      saldoTrend.style.color = diff >= 0 ? "#27ae60" : "#e74c3c"; // Verde para melhora, vermelho para piora
    }
    
  } else if (saldoTrend) {
    saldoTrend.textContent = "Mês inicial";
  }
}

async function loadData() {
  try {
    const [profits, fixedExpenses, varExpenses] = await Promise.all([
      fetchJSON(`${API_BASE}/profits/read`),
      fetchJSON(`${API_BASE}/pinExpenses/read`),
      fetchJSON(`${API_BASE}/varExpenses/read`),
    ]);

    if (profits.length === 0 && fixedExpenses.length === 0 && varExpenses.length === 0) {
      updateCards(0, 0, 0, null);
      const container = document.querySelector(".projection-chart");
      container.innerHTML = `<p style="text-align: center; color: #aaa; grid-column: span 3;">Nenhum dado disponível. Cadastre lucros ou gastos para ver projeções.</p>`;
      return;
    }

    const monthly = {};
    let minDate = new Date();
    let maxDate = new Date(1970, 0, 1);

    const allTransactions = [...profits, ...fixedExpenses, ...varExpenses];
    allTransactions.forEach(t => {
      const startDate = new Date(t.data);
      if (startDate < minDate) minDate = startDate;
      
      let endDate = new Date(t.data);
      if (t.parcelas > 0) {
        endDate.setMonth(endDate.getMonth() + t.parcelas - 1);
      }
      if (endDate > maxDate) maxDate = endDate;
    });

    const projectionEndDate = new Date(maxDate);
    projectionEndDate.setMonth(projectionEndDate.getMonth() + 5);

    let currentMonth = new Date(minDate.getFullYear(), minDate.getMonth(), 1);
    while (currentMonth <= projectionEndDate) {
      const key = `${currentMonth.getFullYear()}-${String(currentMonth.getMonth() + 1).padStart(2, "0")}`;
      if (!monthly[key]) {
        monthly[key] = { lucros: 0, gastos: 0, saldo: 0 };
      }
      currentMonth.setMonth(currentMonth.getMonth() + 1);
    }
    
    profits.forEach(p => {
      const startDate = new Date(p.data);
      if (p.recorrente) {
        let monthIter = new Date(startDate.getFullYear(), startDate.getMonth(), 1);
        while (monthIter <= projectionEndDate) {
          const key = `${monthIter.getFullYear()}-${String(monthIter.getMonth() + 1).padStart(2, "0")}`;
          if (monthly[key]) {
            monthly[key].lucros += p.valor;
          }
          monthIter.setMonth(monthIter.getMonth() + 1);
        }
      } else {
        const key = `${startDate.getFullYear()}-${String(startDate.getMonth() + 1).padStart(2, "0")}`;
        if (monthly[key]) {
          monthly[key].lucros += p.valor;
        }
      }
    });

    fixedExpenses.forEach(e => {
      const startDate = new Date(e.data);
      let monthIter = new Date(startDate.getFullYear(), startDate.getMonth(), 1);
      while (monthIter <= projectionEndDate) {
        const key = `${monthIter.getFullYear()}-${String(monthIter.getMonth() + 1).padStart(2, "0")}`;
        if (monthly[key]) {
          monthly[key].gastos += e.valor;
        }
        monthIter.setMonth(monthIter.getMonth() + 1);
      }
    });

    varExpenses.forEach(e => {
      const startDate = new Date(e.data);
      const installmentValue = e.valor / e.parcelas;
      for (let i = 0; i < e.parcelas; i++) {
        const installmentDate = new Date(startDate);
        installmentDate.setMonth(startDate.getMonth() + i);
        const key = `${installmentDate.getFullYear()}-${String(installmentDate.getMonth() + 1).padStart(2, "0")}`;
        if (monthly[key]) {
          monthly[key].gastos += installmentValue;
        }
      }
    });

    // =========================================================================
    // NOVA LÓGICA: CÁLCULO DO SALDO CUMULATIVO
    // =========================================================================
    
    // 1. Pega as chaves dos meses e ordena cronologicamente (ex: '2023-11', '2023-12', '2024-01')
    const sortedMonths = Object.keys(monthly).sort();
    
    let cumulativeBalance = 0; // Inicia o saldo acumulado como 0

    // 2. Itera sobre cada mês em ordem para calcular o saldo acumulado
    sortedMonths.forEach(key => {
        const monthData = monthly[key];
        // O saldo final deste mês é o saldo acumulado do mês anterior + lucros - gastos
        monthData.saldo = cumulativeBalance + monthData.lucros - monthData.gastos;
        // Atualiza o saldo acumulado para ser usado no próximo mês
        cumulativeBalance = monthData.saldo;
    });
    
    // =========================================================================

    // Agora que os saldos estão corretos, converte e ordena para renderização (mais recente primeiro)
    const projections = Object.entries(monthly)
      .map(([mes, dados]) => ({ mes, ...dados }))
      .sort((a, b) => new Date(b.mes) - new Date(a.mes));

    // Atualiza os cards do topo com base no mês ATUAL
    const today = new Date();
    const currentMonthKey = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, "0")}`;
    const prevMonth = new Date();
    prevMonth.setMonth(prevMonth.getMonth() - 1);
    const prevMonthKey = `${prevMonth.getFullYear()}-${String(prevMonth.getMonth() + 1).padStart(2, "0")}`;

    // Os dados dos cards devem ser do fluxo do mês, não do saldo acumulado
    const currentData = monthly[currentMonthKey] || { saldo: 0, lucros: 0, gastos: 0 };
    const prevData = monthly[prevMonthKey] || null;
    
    // O saldo no card do topo deve ser o saldo final (acumulado) do mês atual.
    // Lucros e gastos devem ser os valores do mês.
    updateCards(currentData.saldo, currentData.lucros, currentData.gastos, prevData ? prevData.saldo : null);
    
    // Renderiza todas as projeções
    const container = document.querySelector(".projection-chart");
    container.innerHTML = "";

    projections.forEach((proj, index) => {
      const previous = projections[index + 1] || null;
      renderMonth(proj, previous);
    });

  } catch (err) {
    console.error("Erro em loadData:", err);
  }
}

function renderMonth(current, previous) {
  const container = document.querySelector(".projection-chart");
  const monthDiv = document.createElement("div");
  monthDiv.className = "month-projection";

  const header = document.createElement("div");
  header.className = "month-header";
  const h4 = document.createElement("h4");
  h4.textContent = formatMonth(current.mes);

  const status = document.createElement("span");
  const today = new Date();
  const currentMonthDate = new Date(current.mes + "-02");
  
  if (currentMonthDate.getFullYear() < today.getFullYear() || (currentMonthDate.getFullYear() === today.getFullYear() && currentMonthDate.getMonth() < today.getMonth())) {
    status.className = "status neutral";
    status.textContent = "Realizado";
  } else if (currentMonthDate.getFullYear() === today.getFullYear() && currentMonthDate.getMonth() === today.getMonth()) {
    status.className = "status warning";
    status.textContent = "Atual";
  } else {
    status.className = "status positive";
    status.textContent = "Projetado";
  }

  header.appendChild(h4);
  header.appendChild(status);
  monthDiv.appendChild(header);

  const details = document.createElement("div");
  details.className = "projection-details";

  details.appendChild(makeItem("Lucros do Mês:", current.lucros, "positive"));
  details.appendChild(makeItem("Gastos do Mês:", current.gastos, "negative"));
  details.appendChild(makeItem("Saldo Final:", current.saldo, current.saldo >= 0 ? "positive" : "negative"));

  // A comparação de tendência agora reflete a variação do SALDO ACUMULADO
  if (previous) {
    const trend = document.createElement("p");
    trend.className = "trend";
    const prevSaldo = previous.saldo;
    
    // O "saldo do mês" é a diferença entre o saldo final atual e o anterior
    const monthNetBalance = current.saldo - prevSaldo;

    trend.textContent = `Resultado do mês: R$ ${monthNetBalance.toFixed(2)}`;
    trend.style.color = monthNetBalance >= 0 ? "green" : "red";
    details.appendChild(trend);
  }

  monthDiv.appendChild(details);
  container.appendChild(monthDiv);
}

function makeItem(label, value, cls) {
  const item = document.createElement("div");
  item.className = "projection-item";
  
  const spanLabel = document.createElement("span");
  spanLabel.textContent = label;
  const spanValue = document.createElement("span");
  spanValue.textContent = `R$ ${value.toFixed(2)}`;
  spanValue.className = `amount ${cls}`;

  item.appendChild(spanLabel);
  item.appendChild(spanValue);
  
  return item;
}

function formatMonth(key) {
  const [y, m] = key.split("-");
  const nomes = ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"];
  return `${nomes[parseInt(m) - 1]} ${y}`;
}

document.addEventListener("DOMContentLoaded", loadData);