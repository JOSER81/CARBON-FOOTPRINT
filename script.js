const $ = id => document.getElementById(id);

const API_URL = 'http://localhost:8080';

const CORES = [
  'var(--c0)',
  'var(--c1)',
  'var(--c2)',
  'var(--c3)',
  'var(--c4)'
];

let usuarios = [];

async function api(url, metodo = 'GET', corpo) {
  const r = await fetch(`${API_URL}${url}`, {
    method: metodo,
    headers: {
      'Content-Type': 'application/json'
    },
    body: corpo ? JSON.stringify(corpo) : undefined
  });

  const dados = await r.json();

  if (!r.ok) {
    throw new Error(dados.erro || 'Algo deu errado.');
  }

  return dados;
}

async function testarConexao() {
  try {
    const resposta = await fetch(`${API_URL}/api/status`);
    const dados = await resposta.json();

    if (!resposta.ok) {
      throw new Error('Erro ao conectar com a API.');
    }

    console.log('Conexão com o Java:', dados);
    aviso(dados.mensagem, false);

  } catch (erro) {
    aviso('Não foi possível conectar ao back-end Java.', true);
    console.error(erro);
  }
}

function aviso(msg, erro) {
  const t = $('toast');

  t.textContent = msg;
  t.className = 'on' + (erro ? ' erro' : '');

  clearTimeout(aviso.t);

  aviso.t = setTimeout(() => {
    t.className = '';
  }, 3200);
}

const tratar = fn => async (...a) => {
  try {
    await fn(...a);
  } catch (e) {
    aviso(e.message, true);
  }
};

const esc = s => String(s).replace(/[&<>"]/g, c => ({
  '&': '&amp;',
  '<': '&lt;',
  '>': '&gt;',
  '"': '&quot;'
}[c]));

const nivelCss = c =>
  c.startsWith('BAIXA') ? 'n-baixa' :
  c.startsWith('M') ? 'n-media' :
  'n-alta';

const dataBR = d =>
  new Date(d).toLocaleString('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'short'
  });

document.querySelectorAll('.nav').forEach(b => {
  b.onclick = () => {
    document.querySelectorAll('.nav, .tab').forEach(e => {
      e.classList.remove('on');
    });

    b.classList.add('on');
    $(b.dataset.tab).classList.add('on');

    if (b.dataset.tab === 'historico') {
      tratar(carregarHistorico)();
    }
  };
});

async function carregarUsuarios() {
  usuarios = await api('/api/usuarios');

  $('listaUsuarios').innerHTML = usuarios.length
    ? usuarios.map(u => `
      <tr>
        <td>${esc(u.nome)}</td>
        <td>${esc(u.email)}</td>
        <td>${esc(u.cidade)}</td>
        <td class="fim">
          <button class="btn mini ghost" data-ed="${u.id}">Editar</button>
          <button class="btn mini perigo" data-del="${u.id}">Excluir</button>
        </td>
      </tr>
    `).join('')
    : '<tr><td colspan="4" class="vazio">Nenhum usuário ainda. Cadastre o primeiro acima.</td></tr>';

  const op = usuarios.map(u =>
    `<option value="${u.id}">${esc(u.nome)}</option>`
  ).join('');

  $('cUsuario').innerHTML =
    op || '<option value="">Cadastre um usuário primeiro</option>';

  const atual = $('hUsuario').value;

  $('hUsuario').innerHTML =
    '<option value="">Todos</option>' + op;

  $('hUsuario').value = atual;
}

function limparFormUsuario() {
  $('formUsuario').reset();
  $('uId').value = '';
  $('uSalvar').textContent = 'Cadastrar usuário';
  $('uCancelar').hidden = true;
}

$('uCancelar').onclick = limparFormUsuario;

$('formUsuario').onsubmit = tratar(async e => {
  e.preventDefault();

  const dados = {
    nome: $('uNome').value,
    email: $('uEmail').value,
    cidade: $('uCidade').value
  };

  const id = $('uId').value;

  await api(
    id ? `/api/usuarios/${id}` : '/api/usuarios',
    id ? 'PUT' : 'POST',
    dados
  );

  aviso(id ? 'Usuário atualizado.' : 'Usuário cadastrado.');

  limparFormUsuario();

  await carregarUsuarios();
});

$('listaUsuarios').onclick = tratar(async e => {
  const ed = e.target.dataset.ed;
  const del = e.target.dataset.del;

  if (ed) {
    const u = usuarios.find(x => x.id == ed);

    $('uId').value = u.id;
    $('uNome').value = u.nome;
    $('uEmail').value = u.email;
    $('uCidade').value = u.cidade;

    $('uSalvar').textContent = 'Salvar alterações';
    $('uCancelar').hidden = false;

    $('uNome').focus();
  }

  if (del && confirm('Excluir este usuário e todos os cálculos dele?')) {
    await api(`/api/usuarios/${del}`, 'DELETE');

    aviso('Usuário excluído.');

    await carregarUsuarios();
  }
});

function mostrarResultado(c, alvo) {
  const total = c.fatores.reduce((s, f) => s + f.valor, 0) || 1;

  alvo.innerHTML = `
    <div class="card">
      <div>
        <span class="total">${c.total.toFixed(1)} kg CO₂</span>
        <span class="nivel ${nivelCss(c.classificacao)}">
          ${esc(c.classificacao)}
        </span>
      </div>

      <div class="estratos" aria-hidden="true">
        ${c.fatores.map((f, i) =>
          `<span style="width:${f.valor / total * 100}%;background:${CORES[i % 5]}"></span>`
        ).join('')}
      </div>

      <div class="legenda">
        ${c.fatores.map((f, i) => `
          <div>
            <i style="background:${CORES[i % 5]}"></i>
            ${esc(f.nome)}:
            <b>${f.valor.toFixed(1)} kg</b>
            <small>(${esc(f.descricao)})</small>
          </div>
        `).join('')}
      </div>

      <p class="dica">${esc(c.dica)}</p>
    </div>
  `;
}

$('formCalculo').onsubmit = tratar(async e => {
  e.preventDefault();

  const c = await api('/api/calculos', 'POST', {
    usuarioId: $('cUsuario').value,
    km: $('cKm').value,
    transporte: $('cTransporte').value,
    energia: $('cEnergia').value,
    alimentacao: $('cAlimentacao').value,
    consumo: $('cConsumo').value,
    residuos: $('cResiduos').value
  });

  mostrarResultado(c, $('resultado'));

  aviso('Cálculo salvo no histórico.');
});

async function carregarHistorico() {
  const uid = $('hUsuario').value;

  const lista = await api(
    '/api/calculos' + (uid ? `?usuarioId=${uid}` : '')
  );

  $('detalhe').innerHTML = '';

  $('listaCalculos').innerHTML = lista.length
    ? lista.map(c => `
      <tr>
        <td>${dataBR(c.data)}</td>
        <td>${esc(c.usuario)}</td>
        <td>${c.total.toFixed(1)} kg</td>
        <td>
          <span class="nivel ${nivelCss(c.classificacao)}" style="margin:0">
            ${esc(c.classificacao)}
          </span>
        </td>
        <td class="fim">
          <button class="btn mini ghost" data-ver="${c.id}">Detalhes</button>
          <button class="btn mini perigo" data-del="${c.id}">Excluir</button>
        </td>
      </tr>
    `).join('')
    : '<tr><td colspan="5" class="vazio">Nenhum cálculo registrado. Faça o primeiro na aba Calcular.</td></tr>';
}

$('hUsuario').onchange = tratar(carregarHistorico);

$('listaCalculos').onclick = tratar(async e => {
  const ver = e.target.dataset.ver;
  const del = e.target.dataset.del;

  if (ver) {
    mostrarResultado(
      await api(`/api/calculos/${ver}`),
      $('detalhe')
    );
  }

  if (del && confirm('Excluir este cálculo?')) {
    await api(`/api/calculos/${del}`, 'DELETE');

    aviso('Cálculo excluído.');

    await carregarHistorico();
  }
});

testarConexao();