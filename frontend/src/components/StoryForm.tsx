import { useState } from 'react'
import type { Department, StoryDto } from '../types'

interface Props {
  departments: Department[]
  onSubmit: (dto: StoryDto) => void
  initial?: StoryDto
  submitLabel?: string
}

export function StoryForm({ departments, onSubmit, initial, submitLabel = 'Add Story' }: Props) {
  const [title, setTitle] = useState(initial?.title ?? '')
  const [description, setDescription] = useState(initial?.description ?? '')
  const [departmentId, setDepartmentId] = useState<number>(
    initial?.departmentId ?? (departments[0]?.id ?? 0)
  )

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!title.trim()) return
    onSubmit({ title, description, departmentId })
    setTitle('')
    setDescription('')
  }

  return (
    <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 8, maxWidth: 400 }}>
      <input
        placeholder="Title"
        value={title}
        onChange={e => setTitle(e.target.value)}
        required
      />
      <textarea
        placeholder="Description"
        value={description}
        onChange={e => setDescription(e.target.value)}
        rows={3}
      />
      <select value={departmentId} onChange={e => setDepartmentId(Number(e.target.value))}>
        {departments.map(d => (
          <option key={d.id} value={d.id}>{d.name}</option>
        ))}
      </select>
      <button type="submit">{submitLabel}</button>
    </form>
  )
}
