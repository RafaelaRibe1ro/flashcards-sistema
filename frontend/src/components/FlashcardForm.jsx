import { useState, useEffect } from 'react'
import { criarFlashcard } from '../services/flashcardApi'
import { listarCategorias } from '../services/categoriaApi'

export default function FlashcardForm({ onCriado }) {
  const [pergunta, setPergunta] = useState('')
  const [resposta, setResposta] = useState('')
  const [categoriaId, setCategoriaId] = useState('')
  const [categorias, setCategorias] = useState([])
  const [erro, setErro] = useState('')

  useEffect(() => {
    listarCategorias()
      .then(setCategorias)
      .catch(() => console.error('Não foi possível carregar as categorias. Verifique se o categoria-service está rodando.'))
  }, [])

  async function handleSubmit(e) {
    e.preventDefault()
    setErro('')
    try {
      await criarFlashcard(pergunta, resposta, categoriaId ? Number(categoriaId) : null)
      setPergunta('')
      setResposta('')
      setCategoriaId('')
      onCriado()
    } catch {
      setErro('Não foi possível criar o flashcard. Verifique se o servidor está rodando.')
    }
  }

  return (
    <form className="card form-card" onSubmit={handleSubmit}>
      <h2>Novo Flashcard</h2>
      {erro && <p className="erro">{erro}</p>}
      <label>
        Pergunta
        <textarea
          value={pergunta}
          onChange={e => setPergunta(e.target.value)}
          required
          rows={3}
          placeholder="Digite a pergunta..."
        />
      </label>
      <label>
        Resposta
        <textarea
          value={resposta}
          onChange={e => setResposta(e.target.value)}
          required
          rows={3}
          placeholder="Digite a resposta..."
        />
      </label>
      <label>
        Categoria
        <select value={categoriaId} onChange={e => setCategoriaId(e.target.value)}>
          <option value="">Sem categoria</option>
          {categorias.map(cat => (
            <option key={cat.id} value={cat.id}>{cat.nome}</option>
          ))}
        </select>
      </label>
      <button type="submit">Adicionar Flashcard</button>
    </form>
  )
}
