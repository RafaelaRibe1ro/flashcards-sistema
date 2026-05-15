import { useState } from 'react'

export default function StudyMode({ flashcards }) {
  const [indice, setIndice] = useState(0)
  const [mostrandoResposta, setMostrandoResposta] = useState(false)

  if (flashcards.length === 0) {
    return <p className="vazio">Cadastre flashcards antes de começar a estudar.</p>
  }

  const card = flashcards[indice]
  const total = flashcards.length

  function proximo() {
    setIndice(i => (i + 1) % total)
    setMostrandoResposta(false)
  }

  function anterior() {
    setIndice(i => (i - 1 + total) % total)
    setMostrandoResposta(false)
  }

  return (
    <div className="estudo">
      <p className="progresso">Card {indice + 1} de {total}</p>

      <div className="flashcard-estudo">
        <span className="field-label">Pergunta</span>
        <p className="pergunta-estudo">{card.pergunta}</p>

        {mostrandoResposta ? (
          <div className="resposta-area">
            <span className="field-label">Resposta</span>
            <p className="resposta-estudo">{card.resposta}</p>
          </div>
        ) : (
          <button className="btn-revelar" onClick={() => setMostrandoResposta(true)}>
            Ver Resposta
          </button>
        )}
      </div>

      <div className="navegacao">
        <button onClick={anterior}>← Anterior</button>
        <button onClick={proximo}>Próximo →</button>
      </div>
    </div>
  )
}
