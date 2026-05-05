import { apiFetch } from './client'
import type { Story, StoryDto } from '../types'

export const getStories = () => apiFetch<Story[]>('/stories')

export const createStory = (dto: StoryDto) =>
  apiFetch<Story>('/stories', {
    method: 'POST',
    body: JSON.stringify(dto),
  })

export const updateStory = (id: number, dto: StoryDto) =>
  apiFetch<Story>(`/stories/${id}`, {
    method: 'PUT',
    body: JSON.stringify(dto),
  })

export const deleteStory = (id: number) =>
  apiFetch<void>(`/stories/${id}`, { method: 'DELETE' })
