import { deletarFlashcard } from '../services/flashcardApi'

export default function FlashcardList({ flashcards, onDeletado }) {
  async function handleDeletar(id) {
    if (!window.confirm('Deseja excluir este flashcard?')) return
    try {
      await deletarFlashcard(id)
      onDeletado()
    } catch {
      alert('Erro ao excluir flashcard.')
    }
  }

  if (flashcards.length === 0) {
    return <p className="vazio">Nenhum flashcard cadastrado ainda.</p>
  }

  return (
    <div className="lista">
      <h2>Meus Flashcards ({flashcards.length})</h2>
      {flashcards.map(fc => (
        <div key={fc.id} className="card list-item">
          <div className="list-item-content">
            <span className="field-label">Pergunta</span>
            <p>{fc.pergunta}</p>
            <span className="field-label">Resposta</span>
            <p>{fc.resposta}</p>
          </div>
          <button className="btn-deletar" onClick={() => handleDeletar(fc.id)}>
            Excluir
          </button>
        </div>
      ))}
    </div>
  )
}
