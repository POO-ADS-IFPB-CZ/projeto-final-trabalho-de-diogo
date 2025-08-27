const API_URL = 'http://localhost:8080/profits';

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
  fetchLucros();
  document.getElementById('new').addEventListener('click', () => openModal({ mode: 'create' }));
}

// ---------- FETCH CRUD ----------
async function fetchLucros() {
  try {
    const res = await fetch(`${API_URL}/read`);
    if (!res.ok) throw new Error('Falha ao obter lucros');
    const lucros = await res.json();
    renderTable(lucros);
  } catch (err) {
    alert(err.message);
  }
}

async function createLucro(lucro) {
  const res = await fetch(`${API_URL}/create`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(lucro),
  });
  if (!res.ok) throw new Error('Erro ao criar lucro.');
  return await res.json();
}

async function updateLucro(id, lucro) {
  const res = await fetch(`${API_URL}/update-${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(lucro),
  });
  if (!res.ok) throw new Error('Erro ao atualizar lucro.');
  return await res.json();
}

async function deleteLucro(id) {
  const res = await fetch(`${API_URL}/delete-${id}`, { method: 'DELETE' });
  if (!res.ok) throw new Error('Erro ao deletar lucro.');
  return true;
}

// ---------- RENDER TABLE ----------
function renderTable(lucros) {
  const tbody = document.querySelector('table tbody');
  tbody.innerHTML = '';
  lucros.sort((a, b) => new Date(b.data) - new Date(a.data));
  lucros.forEach(l => tbody.appendChild(createRow(l)));
  calculateTotal(lucros);
}

function createRow(lucro) {
  const tr = document.createElement('tr');
  tr.dataset.id = lucro.id;

  const tdDesc = document.createElement('td');
  tdDesc.classList.add('expand');
  tdDesc.textContent = lucro.descricao || '';

  const tdRecorrente = document.createElement('td');
  tdRecorrente.textContent = lucro.recorrente ? 'Sim' : 'Não';

  const tdData = document.createElement('td');
  tdData.textContent = formatDateBR(lucro.data);

  const tdValor = document.createElement('td');
  tdValor.textContent = formatCurrency(lucro.valor);

  const tdAcoes = document.createElement('td');

  const editButton = document.createElement('button');
  editButton.setAttribute('data-action', 'edit');
  editButton.innerText = 'Editar';
  editButton.addEventListener('click', () => openModal({ mode: 'edit', lucro }));

  const deleteButton = document.createElement('button');
  deleteButton.setAttribute('data-action', 'delete');
  deleteButton.innerText = 'Excluir';
  deleteButton.addEventListener('click', async () => {
    await deleteLucro(lucro.id);
    await fetchLucros();
  });

  tdAcoes.appendChild(editButton);
  tdAcoes.appendChild(deleteButton);

  tr.appendChild(tdDesc);
  tr.appendChild(tdRecorrente);
  tr.appendChild(tdData);
  tr.appendChild(tdValor);
  tr.appendChild(tdAcoes);

  return tr;
}

// ----- MODAL (criado dinamicamente) -----
function openModal({ mode = 'create', lucro = null }) {
  const existing = document.querySelector('.lucro-modal');
  if (existing) existing.remove();

  const modal = document.createElement('div');
  modal.className = 'lucro-modal gasto-modal'; // usa a classe do css antigo

  const backdrop = document.createElement("div");
  backdrop.className = "modal-backdrop";

  const card = document.createElement("div");
  card.className = "modal-card";

  const h2 = document.createElement("h2");
  h2.textContent = mode === "create" ? "Novo Lucro" : "Editar Lucro";

  const formulario = document.createElement("form");
  formulario.className = "gasto-form"; // usa a classe do css antigo

  // Descrição
  const labelDesc = document.createElement("label");
  labelDesc.innerText = 'Descrição';
  const inputDesc = document.createElement("input");
  inputDesc.name = "descricao";
  inputDesc.required = true;
  inputDesc.maxLength = 120;
  inputDesc.placeholder = "Ex: Salário, Projeto Freelance";
  labelDesc.appendChild(inputDesc);
  
  // Recorrente (Checkbox)
  const labelRecorrente = document.createElement("label");
  labelRecorrente.className = "checkbox-label";
  const inputRecorrente = document.createElement("input");
  inputRecorrente.name = "recorrente";
  inputRecorrente.type = "checkbox";
  labelRecorrente.appendChild(inputRecorrente);
  labelRecorrente.append(" Recorrente"); // Adiciona o texto ao lado do checkbox

  // Data
  const labelData = document.createElement("label");
  labelData.innerText = 'Data';
  const inputData = document.createElement("input");
  inputData.name = "data";
  inputData.type = "text"; // Usar text para permitir formato DD/MM/YYYY
  inputData.placeholder = "DD/MM/YYYY";
  inputData.required = true;
  labelData.appendChild(inputData);

  // Valor
  const labelValor = document.createElement("label");
  labelValor.innerText = 'Valor (R$)';
  const inputValor = document.createElement("input");
  inputValor.name = "valor";
  inputValor.type = "number";
  inputValor.step = "0.01";
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
  
  formulario.appendChild(labelDesc);
  formulario.appendChild(labelRecorrente);
  formulario.appendChild(labelData);
  formulario.appendChild(labelValor);
  formulario.appendChild(actions);

  card.appendChild(h2);
  card.appendChild(formulario);
  modal.appendChild(backdrop);
  modal.appendChild(card);
  document.body.appendChild(modal);
  
  // preenche se for modo de edição
  if (lucro) {
    formulario.descricao.value = lucro.descricao || '';
    formulario.recorrente.checked = lucro.recorrente || false;
    formulario.data.value = lucro.data ? formatDateBR(lucro.data) : '';
    formulario.valor.value = lucro.valor != null ? lucro.valor : '';
  }

  // eventos
  btnCancel.addEventListener('click', () => closeModal(modal));
  backdrop.addEventListener('click', () => closeModal(modal));

  formulario.addEventListener('submit', async (ev) => {
    ev.preventDefault();
    const isoData = convertToISO(formulario.data.value);
    if (!isoData) {
      alert('Data inválida! Use o formato DD/MM/YYYY.');
      formulario.data.focus();
      return;
    }

    const payload = {
      descricao: formulario.descricao.value.trim(),
      recorrente: formulario.recorrente.checked,
      data: isoData,
      valor: parseFloat(formulario.valor.value) || 0,
    };

    try {
      if (mode === 'create') {
        await createLucro(payload);
      } else {
        await updateLucro(lucro.id, payload);
      }
      await fetchLucros();
      closeModal(modal);
    } catch (err) {
      console.error(err);
      alert(err.message);
    }
  });

  setTimeout(() => formulario.descricao.focus(), 50);
}

function closeModal(modal) {
  modal.remove();
}

// ----- UTILITÁRIOS -----
function formatCurrency(value) {
  if (value == null || isNaN(Number(value))) return 'R$ 0,00';
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value));
}

function formatDateBR(isoDate) {
  if (!isoDate) return '';
  const datePart = isoDate.split('T')[0];
  const parts = datePart.split('-');
  if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
  return isoDate;
}

function calculateTotal(lucros) {
  const total = lucros.reduce((acc, l) => acc + (Number(l.valor) || 0), 0);
  const el = document.getElementById('totalValue');
  if (el) el.textContent = formatCurrency(total);
}

// Inicia a aplicação
init();