export interface Department {
  id: number
  name: string
}

export interface Story {
  id: number
  title: string
  description: string
  departmentId: number
}

export interface StoryDto {
  title: string
  description: string
  departmentId: number
}
