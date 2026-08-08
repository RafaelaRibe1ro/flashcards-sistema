import { deletarCategoria } from '../services/categoriaApi'

export default function CategoriaList({ categorias, onDeletada }) {
  async function handleDeletar(id) {
    if (!window.confirm('Deseja excluir esta categoria?')) return
    try {
      await deletarCategoria(id)
      onDeletada()
    } catch {
      alert('Erro ao excluir categoria.')
    }
  }

  if (categorias.length === 0) {
    return <p className="vazio">Nenhuma categoria cadastrada ainda.</p>
  }

  return (
    <div className="lista">
      <h2>Categorias ({categorias.length})</h2>
      {categorias.map(cat => (
        <div key={cat.id} className="card list-item">
          <div className="list-item-content">
            <span className="field-label">Nome</span>
            <p>{cat.nome}</p>
            {cat.descricao && (
              <>
                <span className="field-label">Descrição</span>
                <p>{cat.descricao}</p>
              </>
            )}
          </div>
          <div className="item-actions">
            <button className="btn-deletar" onClick={() => handleDeletar(cat.id)}>Excluir</button>
          </div>
        </div>
      ))}
    </div>
  )
}
