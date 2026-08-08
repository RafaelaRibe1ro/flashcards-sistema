const BASE_URL = 'http://localhost:8080/api/flashcards'

export async function listarFlashcards() {
  const res = await fetch(BASE_URL)
  if (!res.ok) throw new Error('Erro ao buscar flashcards')
  return res.json()
}

export async function criarFlashcard(pergunta, resposta, categoriaId) {
  const res = await fetch(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ pergunta, resposta, categoriaId })
  })
  if (!res.ok) throw new Error('Erro ao criar flashcard')
  return res.json()
}

export async function atualizarFlashcard(id, pergunta, resposta, categoriaId) {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ pergunta, resposta, categoriaId })
  })
  if (!res.ok) throw new Error('Erro ao atualizar flashcard')
  return res.json()
}

export async function deletarFlashcard(id) {
  const res = await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' })
  if (!res.ok) throw new Error('Erro ao deletar flashcard')
}
