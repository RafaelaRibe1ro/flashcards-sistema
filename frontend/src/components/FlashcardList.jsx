import { useState, useEffect } from 'react'
import { deletarFlashcard, atualizarFlashcard } from '../services/flashcardApi'
import { listarCategorias } from '../services/categoriaApi'

export default function FlashcardList({ flashcards, onDeletado }) {
  const [editandoId, setEditandoId] = useState(null)
  const [perguntaEdit, setPerguntaEdit] = useState('')
  const [respostaEdit, setRespostaEdit] = useState('')
  const [categoriaIdEdit, setCategoriaIdEdit] = useState('')
  const [categorias, setCategorias] = useState([])

  useEffect(() => {
    listarCategorias().catch(() => []).then(dados => setCategorias(dados || []))
  }, [])

  function abrirEdicao(fc) {
    setEditandoId(fc.id)
    setPerguntaEdit(fc.pergunta)
    setRespostaEdit(fc.resposta)
    setCategoriaIdEdit(fc.categoriaId ?? '')
  }

  function cancelarEdicao() {
    setEditandoId(null)
  }

  async function handleAtualizar(id) {
    try {
      await atualizarFlashcard(id, perguntaEdit, respostaEdit, categoriaIdEdit ? Number(categoriaIdEdit) : null)
      setEditandoId(null)
      onDeletado()
    } catch {
      alert('Erro ao atualizar flashcard.')
    }
  }

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
          {editandoId === fc.id ? (
            <div className="list-item-content">
              <span className="field-label">Pergunta</span>
              <textarea
                value={perguntaEdit}
                onChange={e => setPerguntaEdit(e.target.value)}
                rows={2}
              />
              <span className="field-label">Resposta</span>
              <textarea
                value={respostaEdit}
                onChange={e => setRespostaEdit(e.target.value)}
                rows={3}
              />
              <span className="field-label">Categoria</span>
              <select value={categoriaIdEdit} onChange={e => setCategoriaIdEdit(e.target.value)}>
                <option value="">Sem categoria</option>
                {categorias.map(cat => (
                  <option key={cat.id} value={cat.id}>{cat.nome}</option>
                ))}
              </select>
              <div className="edit-actions">
                <button className="btn-salvar" onClick={() => handleAtualizar(fc.id)}>Salvar</button>
                <button className="btn-cancelar" onClick={cancelarEdicao}>Cancelar</button>
              </div>
            </div>
          ) : (
            <>
              <div className="list-item-content">
                <span className="field-label">Pergunta</span>
                <p>{fc.pergunta}</p>
                <span className="field-label">Resposta</span>
                <p>{fc.resposta}</p>
                {fc.categoriaNome && (
                  <span className="categoria-badge">{fc.categoriaNome}</span>
                )}
                {fc.atualizadoEm && (
                  <span className="field-label atualizado">
                    Atualizado em: {new Date(fc.atualizadoEm).toLocaleString('pt-BR')}
                  </span>
                )}
              </div>
              <div className="item-actions">
                <button className="btn-editar" onClick={() => abrirEdicao(fc)}>Editar</button>
                <button className="btn-deletar" onClick={() => handleDeletar(fc.id)}>Excluir</button>
              </div>
            </>
          )}
        </div>
      ))}
    </div>
  )
}
