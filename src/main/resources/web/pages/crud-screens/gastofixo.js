const API_URL = 'http://localhost:8080/pinExpenses';

function convertToISO(dateStr) {
  // espera formato DD/MM/YYYY
  if (!dateStr) return null;
  const parts = dateStr.split('/');
  if (parts.length !== 3) return null;
  const day = parts[0].padStart(2, '0');
  const month = parts[1].padStart(2, '0');
  const year = parts[2];
  if (isNaN(day) || isNaN(month) || isNaN(year)) return null;
  if (Number(day) > 31 || Number(day) < 0 || Number(month) > 12 || Number(month) < 0 || Number(year) < 1990) return null;
  return `${year}-${month}-${day}`;
}

function init() {
  fetchGastos();
  document.getElementById('new').addEventListener('click', () => openModal({ mode: 'create' }));
}

// ---------- FETCH CRUD ----------
async function fetchGastos() {
  try {
    const res = await fetch(`${API_URL}/read`);
    if (!res.ok) throw new Error('Falha ao obter gastos');
    const gastos = await res.json();
    renderTable(gastos);
  } catch (err) {
    alert(err);
  }
}

async function createGasto(gasto) {
  const res = await fetch(`${API_URL}/create`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(gasto),
  });
  if (!res.ok) throw new Error('Erro ao criar gasto.');
  return await res.json();
}

async function updateGasto(id, gasto) {
  const res = await fetch(`${API_URL}/update-${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(gasto),
  });
  if (!res.ok) throw new Error('Erro ao atualizar gasto.');
  return await res.json();
}

async function deleteGasto(id) {
  const res = await fetch(`${API_URL}/delete-${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error('Erro ao deletar gasto.');
  return true;
}

// ---------- RENDER TABLE ----------
function renderTable(gastos) {
  const tbody = document.querySelector('table tbody');
  tbody.innerHTML = '';
  gastos.sort((a, b) => new Date(b.data) - new Date(a.data));
  gastos.forEach(g => tbody.appendChild(createRow(g)));
  calculateTotal(gastos);
}

function createRow(gasto) {
  const tr = document.createElement('tr');
  tr.dataset.id = gasto.id;

  const tdDesc = document.createElement('td');
  tdDesc.classList.add('expand');
  tdDesc.textContent = gasto.descricao || '';

  const tdData = document.createElement('td');
  tdData.textContent = formatDateBR(gasto.data);

  const tdValor = document.createElement('td');
  tdValor.textContent = formatCurrency(gasto.valor);

  const tdAcoes = document.createElement('td');

  const editButton = document.createElement('button');
  editButton.setAttribute('data-action', 'edit');
  editButton.innerText = 'Editar';
  editButton.addEventListener('click', () => {
    openModal({ mode: 'edit', gasto });
  });

  const deleteButton = document.createElement('button');
  deleteButton.setAttribute('data-action', 'delete');
  deleteButton.innerText = 'Excluir';
  deleteButton.addEventListener('click', async () => {
    await deleteGasto(gasto.id);
    await fetchGastos();
  });

  tdAcoes.appendChild(editButton);
  tdAcoes.appendChild(deleteButton);

  tr.appendChild(tdDesc);
  tr.appendChild(tdData);
  tr.appendChild(tdValor);
  tr.appendChild(tdAcoes);

  return tr;
}

// ----- MODAL (criado dinamicamente) -----
function openModal({ mode = 'create', gasto = null }) {
  // se já existir modal, remove
  const existing = document.querySelector('.gasto-modal');
  if (existing) existing.remove();

  const modal = document.createElement('div');
  modal.className = 'gasto-modal';

  // Backdrop
  const backdrop = document.createElement("div");
  backdrop.className = "modal-backdrop";

  // Card
  const card = document.createElement("div");
  card.className = "modal-card";

  // Título
  const h2 = document.createElement("h2");
  h2.textContent = mode === "create" ? "Novo Gasto" : "Editar Gasto";

  // Form
  const formulario = document.createElement("form");
  formulario.className = "gasto-form";

  // Campo descrição
  const labelDesc = document.createElement("label");
  labelDesc.innerText = 'Descrição';
  const inputDesc = document.createElement("input");
  inputDesc.name = "descricao";
  inputDesc.required = true;
  inputDesc.maxLength = 120;
  inputDesc.placeholder = "Compras do mês";
  labelDesc.appendChild(inputDesc);

  // Campo data
  const labelData = document.createElement("label");
  labelData.innerText = 'Data';
  const inputData = document.createElement("input");
  inputData.name = "data";
  inputData.type = "date";
  inputData.placeholder = "DD/MM/YYYY";
  inputData.required = true;
  labelData.appendChild(inputData);

  // Campo valor
  const labelValor = document.createElement("label");
  labelValor.innerText = 'Valor (R$)';
  const inputValor = document.createElement("input");
  inputValor.name = "valor";
  inputValor.type = "number";
  inputValor.step = "0.01";
  inputValor.placeholder = "123.45";
  inputValor.required = true;
  labelValor.appendChild(inputValor);

  // Ações
  const actions = document.createElement("div");
  actions.className = "modal-actions";

  const btnSalvar = document.createElement("button");
  btnSalvar.type = "submit";
  btnSalvar.className = "btn-salvar";
  btnSalvar.textContent = "Salvar";

  const btnCancel = document.createElement("button");
  btnCancel.type = "button";
  btnCancel.className = "btn-cancel";
  btnCancel.textContent = "Cancelar";

  actions.appendChild(btnSalvar);
  actions.appendChild(btnCancel);

  // Monta a hierarquia
  formulario.appendChild(labelDesc);
  formulario.appendChild(labelData);
  formulario.appendChild(labelValor);
  formulario.appendChild(actions);

  card.appendChild(h2);
  card.appendChild(formulario);

  modal.appendChild(backdrop);
  modal.appendChild(card);

  document.body.appendChild(modal);

  function validateDateInput() {
    const iso = convertToISO(inputData.value); // retorna null se inválido
    if (!iso) {
      inputData.style.borderColor = 'blue';
      btnSalvar.disabled = true;
    } else {
      inputData.style.borderColor = '';
      btnSalvar.disabled = false;
    }
  }

  ['input', 'keyup', 'change', 'blur'].forEach(evt => {
    inputData.addEventListener(evt, validateDateInput);
  });

  validateDateInput();

  // preenche se editar
  const form = modal.querySelector('.gasto-form');
  if (gasto) {
    form.descricao.value = gasto.descricao || '';
    // convert ISO to dd/mm/yyyy
    form.data.value = gasto.data
        ? (() => {
          const parts = gasto.data.split('T')[0].split('-'); // ["yyyy", "mm", "dd"]
          return `${parts[2]}/${parts[1]}/${parts[0]}`; // "dd/mm/yyyy"
        })()
        : gasto.data;
    form.valor.value = gasto.valor != null ? gasto.valor : '';
  }

  // eventos
  modal.querySelector('.btn-cancel').addEventListener('click', () => closeModal(modal));
  modal.querySelector('.modal-backdrop').addEventListener('click', () => closeModal(modal));

  form.addEventListener('submit', async (ev) => {
    ev.preventDefault();

    // valida data
    const isoData = convertToISO(form.data.value);
    if (!isoData) {
      alert('Data inválida! Use o formato DD/MM/YYYY.');
      form.data.focus();
      return; // interrompe o envio
    }

    const payload = {
      descricao: form.descricao.value.trim(),
      data: isoData, // agora sempre válido
      valor: parseFloat(form.valor.value) || 0,
    };

    try {
      if (mode === 'create') {
        await createGasto(payload);
      } else {
        await updateGasto(gasto.id, payload);
      }
      await fetchGastos();
      closeModal(modal);
    } catch (err) {
      console.error(err);
    }
  });

  // foco primeiro input
  setTimeout(() => form.descricao.focus(), 50);
}

function closeModal(modal) {
  modal.remove();
}

// ----- UTILITÁRIOS -----
function formatCurrency(value) {
  if (value == null || isNaN(Number(value))) return 'R$ 0,00';
  // assume value em número (ex: 1200.5)
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value));
}

function parseDisplayedCurrency(text) {
  // ex: "R$ 1.200,00" -> 1200.00
  if (!text) return 0;
  const onlyNums = text.replace(/[R$\s\.]/g, '').replace(',', '.');
  const n = parseFloat(onlyNums);
  return isNaN(n) ? 0 : n;
}

function formatDateBR(isoDate) {
  if (!isoDate) return '';
  
  // pega só a parte da data antes do 'T', se houver
  const datePart = isoDate.split('T')[0];
  
  const parts = datePart.split('-'); // ["yyyy", "mm", "dd"]
  if (parts.length === 3) {
    return `${parts[2]}/${parts[1]}/${parts[0]}`; // dd/mm/yyyy
  }
  
  // fallback se não for yyyy-mm-dd
  return isoDate;
}

function calculateTotal(gastos) {
  const total = gastos.reduce((acc, g) => acc + (Number(g.valor) || 0), 0);
  const el = document.getElementById('totalValue');
  if (el) el.textContent = formatCurrency(total);
}

init();