import type { Department, Story, StoryDto } from '../types'
import { StoryCard } from './StoryCard'
import { StoryForm } from './StoryForm'

interface Props {
  department: Department
  stories: Story[]
  departments: Department[]
  onAddStory: (dto: StoryDto) => void
  onDeleteStory: (id: number) => void
  onUpdateStory: (id: number, dto: StoryDto) => void
}

export function DepartmentTab({
  department,
  stories,
  departments,
  onAddStory,
  onDeleteStory,
  onUpdateStory,
}: Props) {
  const deptStories = stories.filter(s => s.departmentId === department.id)

  const handleAdd = (dto: StoryDto) => {
    onAddStory({ ...dto, departmentId: department.id })
  }

  return (
    <div>
      <h3>{department.name}</h3>
      {deptStories.length === 0 && <p style={{ color: '#888' }}>No stories yet.</p>}
      {deptStories.map(story => (
        <StoryCard
          key={story.id}
          story={story}
          departments={departments}
          onDelete={onDeleteStory}
          onUpdate={onUpdateStory}
        />
      ))}
      <hr />
      <h4>Add a story to {department.name}</h4>
      <StoryForm
        departments={departments}
        onSubmit={handleAdd}
        initial={{ title: '', description: '', departmentId: department.id }}
      />
    </div>
  )
}
