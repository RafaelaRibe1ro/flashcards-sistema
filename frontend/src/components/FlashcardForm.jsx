import { useState } from 'react'
import { criarFlashcard } from '../services/flashcardApi'

export default function FlashcardForm({ onCriado }) {
  const [pergunta, setPergunta] = useState('')
  const [resposta, setResposta] = useState('')
  const [erro, setErro] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setErro('')
    try {
      await criarFlashcard(pergunta, resposta)
      setPergunta('')
      setResposta('')
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
      <button type="submit">Adicionar Flashcard</button>
    </form>
  )
}
