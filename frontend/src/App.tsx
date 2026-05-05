import { useEffect, useState } from 'react'
import type { Department, Story, StoryDto } from './types'
import { getDepartments } from './api/departments'
import { getStories, createStory, updateStory, deleteStory } from './api/stories'
import { DepartmentTab } from './components/DepartmentTab'

export default function App() {
  const [departments, setDepartments] = useState<Department[]>([])
  const [stories, setStories] = useState<Story[]>([])
  const [activeTab, setActiveTab] = useState<number | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    Promise.all([getDepartments(), getStories()])
      .then(([depts, strs]) => {
        setDepartments(depts)
        setStories(strs)
        if (depts.length > 0) setActiveTab(depts[0].id)
      })
      .catch(() => setError('Failed to load data. Is the backend running?'))
      .finally(() => setLoading(false))
  }, [])

  const handleAddStory = async (dto: StoryDto) => {
    try {
      const created = await createStory(dto)
      setStories(prev => [...prev, created])
    } catch {
      setError('Failed to create story.')
    }
  }

  const handleUpdateStory = async (id: number, dto: StoryDto) => {
    try {
      const updated = await updateStory(id, dto)
      setStories(prev => prev.map(s => (s.id === id ? updated : s)))
    } catch {
      setError('Failed to update story.')
    }
  }

  const handleDeleteStory = async (id: number) => {
    try {
      await deleteStory(id)
      setStories(prev => prev.filter(s => s.id !== id))
    } catch {
      setError('Failed to delete story.')
    }
  }

  const activeDept = departments.find(d => d.id === activeTab) ?? null

  return (
    <div style={{ fontFamily: 'sans-serif', maxWidth: 800, margin: '0 auto', padding: 24 }}>
      <h1>VIA Tabloid</h1>
      {error && (
        <div style={{ background: '#fee', border: '1px solid red', padding: 8, marginBottom: 16 }}>
          {error}
        </div>
      )}
      <div style={{ display: 'flex', gap: 8, marginBottom: 24 }}>
        {departments.map(d => (
          <button
            key={d.id}
            onClick={() => setActiveTab(d.id)}
            style={{
              padding: '8px 16px',
              background: activeTab === d.id ? '#0066cc' : '#eee',
              color: activeTab === d.id ? '#fff' : '#333',
              border: 'none',
              cursor: 'pointer',
              borderRadius: 4,
            }}
          >
            {d.name}
          </button>
        ))}
      </div>
      {activeDept && (
        <DepartmentTab
          department={activeDept}
          stories={stories}
          departments={departments}
          onAddStory={handleAddStory}
          onDeleteStory={handleDeleteStory}
          onUpdateStory={handleUpdateStory}
        />
      )}
      {loading && <p>Loading departments...</p>}
      {!loading && departments.length === 0 && !error && (
        <p style={{ color: '#888' }}>No departments found. Add one via the API to get started.</p>
      )}
    </div>
  )
}
