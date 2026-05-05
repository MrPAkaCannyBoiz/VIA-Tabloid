import { apiFetch } from './client'
import type { Department } from '../types'

export const getDepartments = () => apiFetch<Department[]>('/departments')

export const createDepartment = (name: string) =>
  apiFetch<Department>('/departments', {
    method: 'POST',
    body: JSON.stringify({ name }),
  })

export const deleteDepartment = (id: number) =>
  apiFetch<void>(`/departments/${id}`, { method: 'DELETE' })
