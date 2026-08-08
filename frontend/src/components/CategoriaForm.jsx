import { useState } from 'react'
import { criarCategoria } from '../services/categoriaApi'

export default function CategoriaForm({ onCriada }) {
  const [nome, setNome] = useState('')
  const [descricao, setDescricao] = useState('')
  const [erro, setErro] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setErro('')
    try {
      await criarCategoria(nome, descricao)
      setNome('')
      setDescricao('')
      onCriada()
    } catch {
      setErro('Não foi possível criar a categoria. Verifique se o categoria-service está rodando (porta 8081).')
    }
  }

  return (
    <form className="card form-card" onSubmit={handleSubmit}>
      <h2>Nova Categoria</h2>
      {erro && <p className="erro">{erro}</p>}
      <label>
        Nome
        <input
          type="text"
          value={nome}
          onChange={e => setNome(e.target.value)}
          required
          placeholder="Ex: Java, Spring Boot, React..."
        />
      </label>
      <label>
        Descrição
        <textarea
          value={descricao}
          onChange={e => setDescricao(e.target.value)}
          rows={2}
          placeholder="Descrição opcional..."
        />
      </label>
      <button type="submit">Adicionar Categoria</button>
    </form>
  )
}
