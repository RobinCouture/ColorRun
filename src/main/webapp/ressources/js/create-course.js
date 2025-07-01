document.addEventListener('DOMContentLoaded', function() {
    // Variables globales
    let map = null;
    let marker = null;
    
    // Éléments du DOM
    const imageUploadSection = document.getElementById('imageUploadSection');
    const courseImageInput = document.getElementById('courseImage');
    const imagePreview = document.getElementById('imagePreview');
    const imagePreviewContainer = document.getElementById('imagePreviewContainer');
    const uploadContent = document.getElementById('uploadContent');
    const imageRemove = document.getElementById('imageRemove');
    const uploadProgress = document.getElementById('uploadProgress');
    
    // Animation d'apparition du formulaire
    initFormAnimation();
    
    // Initialisation de l'upload d'image
    initImageUpload();
    
    // Initialisation de la carte
    initMap();
    
    // Validation en temps réel
    initFormValidation();
    
    /**
     * Animation d'apparition du formulaire
     */
    function initFormAnimation() {
        const formSection = document.querySelector('.form-section');
        formSection.style.opacity = '0';
        formSection.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            formSection.style.transition = 'all 0.6s ease-out';
            formSection.style.opacity = '1';
            formSection.style.transform = 'translateY(0)';
        }, 100);
    }
    
    /**
     * Initialisation de l'upload d'image
     */
    function initImageUpload() {
        if (!imageUploadSection || !courseImageInput) return;
        
        // Clic sur la zone d'upload
        imageUploadSection.addEventListener('click', function(e) {
            if (!imagePreviewContainer.style.display || imagePreviewContainer.style.display === 'none') {
                courseImageInput.click();
            }
        });
        
        // Changement de fichier
        courseImageInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                handleImageFile(file);
            }
        });
        
        // Drag & Drop
        imageUploadSection.addEventListener('dragover', function(e) {
            e.preventDefault();
            e.stopPropagation();
            imageUploadSection.classList.add('dragover');
        });
        
        imageUploadSection.addEventListener('dragleave', function(e) {
            e.preventDefault();
            e.stopPropagation();
            imageUploadSection.classList.remove('dragover');
        });
        
        imageUploadSection.addEventListener('drop', function(e) {
            e.preventDefault();
            e.stopPropagation();
            imageUploadSection.classList.remove('dragover');
            
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                const file = files[0];
                if (file.type.startsWith('image/')) {
                    courseImageInput.files = files;
                    handleImageFile(file);
                } else {
                    showImageError('Veuillez sélectionner un fichier image valide.');
                }
            }
        });
        
        // Bouton de suppression de l'image
        if (imageRemove) {
            imageRemove.addEventListener('click', function(e) {
                e.stopPropagation();
                removeImage();
            });
        }
    }
    
    /**
     * Traitement du fichier image
     */
    function handleImageFile(file) {
        // Vérifications
        if (!file.type.startsWith('image/')) {
            showImageError('Le fichier sélectionné n\'est pas une image.');
            return;
        }
        
        if (file.size > 5 * 1024 * 1024) { // 5MB
            showImageError('L\'image est trop volumineuse. Taille maximale: 5MB.');
            return;
        }
        
        // Vérifier les formats autorisés
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
        if (!allowedTypes.includes(file.type)) {
            showImageError('Format non autorisé. Utilisez JPG, PNG, GIF ou WEBP.');
            return;
        }
        
        // Afficher la progress bar
        showUploadProgress();
        
        // Lire le fichier pour l'aperçu
        const reader = new FileReader();
        reader.onload = function(e) {
            // Simuler un délai d'upload pour l'effet visuel
            setTimeout(() => {
                showImagePreview(e.target.result);
                hideUploadProgress();
            }, 500);
        };
        
        reader.onerror = function() {
            showImageError('Erreur lors de la lecture du fichier.');
            hideUploadProgress();
        };
        
        reader.readAsDataURL(file);
    }
    
    /**
     * Afficher l'aperçu de l'image
     */
    function showImagePreview(imageSrc) {
        if (!imagePreview || !imagePreviewContainer || !uploadContent) return;
        
        imagePreview.src = imageSrc;
        uploadContent.style.display = 'none';
        imagePreviewContainer.style.display = 'block';
        imageUploadSection.style.cursor = 'default';
        
        // Animation d'apparition
        imagePreviewContainer.style.opacity = '0';
        imagePreviewContainer.style.transform = 'scale(0.9)';
        
        setTimeout(() => {
            imagePreviewContainer.style.transition = 'all 0.3s ease';
            imagePreviewContainer.style.opacity = '1';
            imagePreviewContainer.style.transform = 'scale(1)';
        }, 50);
        
        hideImageError();
    }
    
    /**
     * Supprimer l'image
     */
    function removeImage() {
        if (!courseImageInput || !imagePreview || !imagePreviewContainer || !uploadContent) return;
        
        courseImageInput.value = '';
        imagePreview.src = '';
        imagePreviewContainer.style.display = 'none';
        uploadContent.style.display = 'block';
        imageUploadSection.style.cursor = 'pointer';
        hideImageError();
    }
    
    /**
     * Afficher la progress bar
     */
    function showUploadProgress() {
        if (!uploadProgress) return;
        
        uploadProgress.style.display = 'block';
        const progressBar = uploadProgress.querySelector('.progress-bar');
        
        if (progressBar) {
            // Animation de la progress bar
            let width = 0;
            const interval = setInterval(() => {
                width += 10;
                progressBar.style.width = width + '%';
                if (width >= 100) {
                    clearInterval(interval);
                }
            }, 50);
        }
    }
    
    /**
     * Masquer la progress bar
     */
    function hideUploadProgress() {
        if (!uploadProgress) return;
        
        uploadProgress.style.display = 'none';
        const progressBar = uploadProgress.querySelector('.progress-bar');
        if (progressBar) {
            progressBar.style.width = '0%';
        }
    }
    
    /**
     * Afficher une erreur d'image
     */
    function showImageError(message) {
        if (!imageUploadSection) return;
        
        // Supprimer l'ancienne erreur s'il y en a une
        const existingError = imageUploadSection.querySelector('.image-error');
        if (existingError) {
            existingError.remove();
        }
        
        // Créer le message d'erreur
        const errorDiv = document.createElement('div');
        errorDiv.className = 'image-error alert alert-danger mt-2';
        errorDiv.innerHTML = `<i class="fas fa-exclamation-triangle me-2"></i>${message}`;
        
        imageUploadSection.appendChild(errorDiv);
        
        // Supprimer l'erreur après 5 secondes
        setTimeout(() => {
            if (errorDiv.parentNode) {
                errorDiv.remove();
            }
        }, 5000);
    }
    
    /**
     * Masquer l'erreur d'image
     */
    function hideImageError() {
        if (!imageUploadSection) return;
        
        const existingError = imageUploadSection.querySelector('.image-error');
        if (existingError) {
            existingError.remove();
        }
    }
    
    /**
     * Initialisation de la carte
     */
    function initMap() {
        const lieuInput = document.getElementById('lieu');
        if (!lieuInput) return;
        
        let searchTimeout;
        
        lieuInput.addEventListener('input', function() {
            const lieu = this.value.trim();
            
            // Debounce la recherche
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                if (lieu.length >= 3) {
                    searchLocation(lieu);
                } else {
                    hideMap();
                }
            }, 500);
        });
        
        // Si le champ a déjà une valeur au chargement
        if (lieuInput.value && lieuInput.value.trim().length >= 3) {
            setTimeout(() => searchLocation(lieuInput.value.trim()), 1000);
        }
    }
    
    /**
     * Recherche de localisation
     */
    function searchLocation(query) {
        const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&limit=1`;
        
        fetch(url)
            .then(response => response.json())
            .then(data => {
                if (data && data.length > 0) {
                    const location = data[0];
                    showMap(parseFloat(location.lat), parseFloat(location.lon), location.display_name);
                } else {
                    hideMap();
                }
            })
            .catch(error => {
                console.error('Erreur lors de la recherche de localisation:', error);
                hideMap();
            });
    }
    
    /**
     * Afficher la carte
     */
    function showMap(lat, lon, displayName) {
        const mapContainer = document.getElementById('mapContainer');
        const mapInfo = document.getElementById('mapInfo');
        
        if (!mapContainer || !mapInfo) return;
        
        // Afficher le container de la carte
        mapContainer.style.display = 'block';
        mapContainer.classList.add('map-appear');
        
        // Mettre à jour les informations
        mapInfo.innerHTML = `<i class="fas fa-map-marker-alt text-success me-2"></i>Localisation trouvée: ${displayName}`;
        
        // Initialiser la carte si ce n'est pas déjà fait
        if (!map) {
            map = L.map('map').setView([lat, lon], 13);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© OpenStreetMap contributors'
            }).addTo(map);
        } else {
            map.setView([lat, lon], 13);
        }
        
        // Supprimer l'ancien marqueur
        if (marker) {
            map.removeLayer(marker);
        }
        
        // Ajouter le nouveau marqueur
        marker = L.marker([lat, lon]).addTo(map)
            .bindPopup(displayName)
            .openPopup();
    }
    
    /**
     * Masquer la carte
     */
    function hideMap() {
        const mapContainer = document.getElementById('mapContainer');
        const mapInfo = document.getElementById('mapInfo');
        
        if (!mapContainer || !mapInfo) return;
        
        mapContainer.style.display = 'none';
        mapInfo.innerHTML = '<i class="fas fa-map text-primary me-2"></i>Saisissez une ville pour voir la localisation sur la carte';
    }
    
    /**
     * Validation en temps réel
     */
    function initFormValidation() {
        const requiredFields = document.querySelectorAll('input[required], textarea[required]');
        
        requiredFields.forEach(field => {
            field.addEventListener('blur', function() {
                if (this.value.trim() === '') {
                    this.style.borderColor = '#dc3545';
                } else {
                    this.style.borderColor = '#28a745';
                }
            });
            
            field.addEventListener('input', function() {
                if (this.value.trim() !== '') {
                    this.style.borderColor = '#007bff';
                }
            });
        });
        
        // Validation spéciale pour les champs numériques
        const numericFields = document.querySelectorAll('input[type="number"]');
        numericFields.forEach(field => {
            field.addEventListener('input', function() {
                const value = parseFloat(this.value);
                if (isNaN(value) || value <= 0) {
                    this.style.borderColor = '#dc3545';
                } else {
                    this.style.borderColor = '#28a745';
                }
            });
        });
        
        // Validation de la date
        const dateField = document.getElementById('dateHeure');
        if (dateField) {
            dateField.addEventListener('change', function() {
                const selectedDate = new Date(this.value);
                const now = new Date();
                
                if (selectedDate < now) {
                    this.style.borderColor = '#dc3545';
                    showImageError('La date doit être dans le futur.');
                } else {
                    this.style.borderColor = '#28a745';
                }
            });
        }
    }
}); 