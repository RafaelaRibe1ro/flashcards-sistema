import { useState, useEffect } from 'react'
import FlashcardForm from './components/FlashcardForm'
import FlashcardList from './components/FlashcardList'
import StudyMode from './components/StudyMode'
import CategoriaForm from './components/CategoriaForm'
import CategoriaList from './components/CategoriaList'
import { listarFlashcards } from './services/flashcardApi'
import { listarCategorias } from './services/categoriaApi'

export default function App() {
  const [aba, setAba] = useState('gerenciar')
  const [flashcards, setFlashcards] = useState([])
  const [categorias, setCategorias] = useState([])

  async function carregarFlashcards() {
    try {
      const dados = await listarFlashcards()
      setFlashcards(dados)
    } catch {
      console.error('Não foi possível carregar os flashcards. Verifique se o backend está rodando.')
    }
  }

  async function carregarCategorias() {
    try {
      const dados = await listarCategorias()
      setCategorias(dados)
    } catch {
      console.error('Não foi possível carregar as categorias. Verifique se o categoria-service está rodando.')
    }
  }

  useEffect(() => {
    carregarFlashcards()
    carregarCategorias()
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
            className={aba === 'categorias' ? 'ativo' : ''}
            onClick={() => setAba('categorias')}
          >
            Categorias
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
        {aba === 'categorias' && (
          <>
            <CategoriaForm onCriada={carregarCategorias} />
            <CategoriaList categorias={categorias} onDeletada={carregarCategorias} />
          </>
        )}
        {aba === 'estudar' && (
          <StudyMode flashcards={flashcards} />
        )}
      </main>
    </div>
  )
}
