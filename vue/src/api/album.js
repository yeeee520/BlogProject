import request from './request'

export function getPhotos(params = {}) {
  return request.get('/api/album/photos', { params })
}

export function getPhoto(id) {
  return request.get('/api/album/photos/' + id)
}

export function getPhotoDownloadUrl(id) {
  return request.get('/api/album/photos/' + id + '/download-url')
}

export function uploadPhoto(formData) {
  return request.post('/api/album/photos', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function updatePhoto(id, data) {
  return request.put('/api/album/photos/' + id, data)
}

export function deletePhoto(id) {
  return request.delete('/api/album/photos/' + id)
}

export function getTags() {
  return request.get('/api/album/tags')
}

export function batchUploadPhotos(formData) {
  return request.post('/api/album/photos/batch', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function batchUpdateVisibility(photoIds, isPublic) {
  return request.put('/api/album/photos/visibility', { photoIds, isPublic })
}

export function getAlbumNames() {
  return request.get('/api/album/albums')
}
