import { useState } from 'react'
import type { Department, Story, StoryDto } from '../types'
import { StoryForm } from './StoryForm'

interface Props {
  story: Story
  departments: Department[]
  onDelete: (id: number) => void
  onUpdate: (id: number, dto: StoryDto) => void
}

export function StoryCard({ story, departments, onDelete, onUpdate }: Props) {
  const [editing, setEditing] = useState(false)

  const handleUpdate = (dto: StoryDto) => {
    onUpdate(story.id, dto)
    setEditing(false)
  }

  if (editing) {
    return (
      <div style={{ border: '1px solid #ccc', padding: 12, marginBottom: 8 }}>
        <StoryForm
          departments={departments}
          onSubmit={handleUpdate}
          initial={{ title: story.title, description: story.description, departmentId: story.departmentId }}
          submitLabel="Save"
        />
        <button onClick={() => setEditing(false)} style={{ marginTop: 4 }}>Cancel</button>
      </div>
    )
  }

  return (
    <div style={{ border: '1px solid #ccc', padding: 12, marginBottom: 8 }}>
      <h4 style={{ margin: '0 0 4px' }}>{story.title}</h4>
      <p style={{ margin: '0 0 8px', color: '#555' }}>{story.description}</p>
      <button onClick={() => setEditing(true)} style={{ marginRight: 8 }}>Edit</button>
      <button onClick={() => onDelete(story.id)} style={{ color: 'red' }}>Delete</button>
    </div>
  )
}
