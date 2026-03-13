// src/main/resources/static/js/funcionarios.js
const API_URL = '/api/funcionarios';

document.addEventListener('DOMContentLoaded', carregarFuncionarios);

document.getElementById('formFuncionario').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const funcionario = {
        nome: document.getElementById('nome').value,
        cpf: document.getElementById('cpf').value,
        email: document.getElementById('email').value || null,
        cargo: document.getElementById('cargo').value,
        salario: parseFloat(document.getElementById('salario').value)
    };
    
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(funcionario)
        });
        
        if (response.ok) {
            alert('Funcionário cadastrado!');
            limparForm();
            carregarFuncionarios();
        } else {
            alert('Erro ao cadastrar. CPF pode já existir.');
        }
    } catch (error) {
        console.error('Erro:', error);
    }
});

async function carregarFuncionarios() {
    try {
        const response = await fetch(API_URL);
        const funcionarios = await response.json();
        
        const tbody = document.getElementById('tabelaFuncionarios');
        
        if (funcionarios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center">Nenhum funcionário</td></tr>';
            return;
        }
        
        tbody.innerHTML = funcionarios.map(func => `
            <tr>
                <td>${func.id}</td>
                <td>${func.nome}</td>
                <td>${func.cpf}</td>
                <td>${func.email || '-'}</td>
                <td>${func.cargo}</td>
                <td>R$ ${func.salario.toFixed(2)}</td>
                <td>
                    <button class="btn btn-sm btn-warning" onclick="editar(${func.id})">Editar</button>
                    <button class="btn btn-sm btn-danger" onclick="deletar(${func.id})">Excluir</button>
                </td>
            </tr>
        `).join('');
        
    } catch (error) {
        console.error('Erro:', error);
        document.getElementById('tabelaFuncionarios').innerHTML = 
            '<tr><td colspan="7" class="text-center text-danger">Erro ao carregar</td></tr>';
    }
}

async function deletar(id) {
    if (confirm('Tem certeza?')) {
        await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        carregarFuncionarios();
    }
}

function limparForm() {
    document.getElementById('formFuncionario').reset();
}

async function editar(id) {
    const response = await fetch(`${API_URL}/${id}`);
    const func = await response.json();
    
    // Implementar modal de edição se desejar
    alert('Funcionalidade de edição em desenvolvimento');
}