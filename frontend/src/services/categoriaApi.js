const BASE_URL = 'http://localhost:8081/api/categorias'

export async function listarCategorias() {
  const res = await fetch(BASE_URL)
  if (!res.ok) throw new Error('Erro ao buscar categorias')
  return res.json()
}

export async function criarCategoria(nome, descricao) {
  const res = await fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nome, descricao })
  })
  if (!res.ok) throw new Error('Erro ao criar categoria')
  return res.json()
}

export async function deletarCategoria(id) {
  const res = await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' })
  if (!res.ok) throw new Error('Erro ao excluir categoria')
}
