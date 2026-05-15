import { useState, useEffect } from 'react'
import FlashcardForm from './components/FlashcardForm'
import FlashcardList from './components/FlashcardList'
import StudyMode from './components/StudyMode'
import { listarFlashcards } from './services/flashcardApi'

export default function App() {
  const [aba, setAba] = useState('gerenciar')
  const [flashcards, setFlashcards] = useState([])

  async function carregarFlashcards() {
    try {
      const dados = await listarFlashcards()
      setFlashcards(dados)
    } catch {
      console.error('Não foi possível carregar os flashcards. Verifique se o backend está rodando.')
    }
  }

  useEffect(() => {
    carregarFlashcards()
  }, [])

  return (
    <div className="app">
      <header>
        <h1>Sistema de Flashcards</h1>
        <nav>
          <button
            className={aba === 'gerenciar' ? 'ativo' : ''}
            onClick={() => setAba('gerenciar')}
          >
            Gerenciar
          </button>
          <button
            className={aba === 'estudar' ? 'ativo' : ''}
            onClick={() => setAba('estudar')}
          >
            Estudar
          </button>
        </nav>
      </header>

      <main>
        {aba === 'gerenciar' && (
          <>
            <FlashcardForm onCriado={carregarFlashcards} />
            <FlashcardList flashcards={flashcards} onDeletado={carregarFlashcards} />
          </>
        )}
        {aba === 'estudar' && (
          <StudyMode flashcards={flashcards} />
        )}
      </main>
    </div>
  )
}
