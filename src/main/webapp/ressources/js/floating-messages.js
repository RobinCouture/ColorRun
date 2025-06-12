/**
 * FLOATING MESSAGES SYSTEM
 * Système de messages flottants amélioré pour ColorRun
 */

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Floating Messages System - Initialized');
    
    // Créer le container des messages s'il n'existe pas
    let messagesContainer = document.getElementById('floating-messages');
    if (!messagesContainer) {
        messagesContainer = document.createElement('div');
        messagesContainer.id = 'floating-messages';
        messagesContainer.className = 'floating-messages';
        document.body.appendChild(messagesContainer);
    }

    /**
     * Afficher un message flottant
     * @param {string} message - Le message à afficher
     * @param {string} type - Type: 'success', 'error', 'warning', 'info'
     * @param {number} duration - Durée en millisecondes (0 = permanent)
     */
    window.showFloatingMessage = function(message, type = 'info', duration = 5000) {
        const messageId = 'msg-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9);
        
        // Déterminer la classe Bootstrap et l'icône
        let alertClass, iconClass;
        switch (type) {
            case 'success':
                alertClass = 'alert-success';
                iconClass = 'fas fa-check-circle';
                break;
            case 'error':
            case 'danger':
                alertClass = 'alert-danger';
                iconClass = 'fas fa-exclamation-triangle';
                break;
            case 'warning':
                alertClass = 'alert-warning';
                iconClass = 'fas fa-exclamation-circle';
                break;
            case 'info':
            default:
                alertClass = 'alert-info';
                iconClass = 'fas fa-info-circle';
                break;
        }
        
        // Créer l'élément message
        const messageElement = document.createElement('div');
        messageElement.id = messageId;
        messageElement.className = `alert ${alertClass} alert-dismissible fade show`;
        messageElement.setAttribute('role', 'alert');
        
        messageElement.innerHTML = `
            <i class="${iconClass} me-2"></i>
            <span>${message}</span>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;
        
        // Ajouter au container
        messagesContainer.appendChild(messageElement);
        
        // Animation d'entrée
        setTimeout(() => {
            messageElement.style.transform = 'translateX(0)';
            messageElement.style.opacity = '1';
        }, 10);
        
        // Auto-suppression si durée spécifiée
        if (duration > 0) {
            setTimeout(() => {
                removeMessage(messageId);
            }, duration);
        }
        
        // Gestion du bouton de fermeture
        const closeBtn = messageElement.querySelector('.btn-close');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                removeMessage(messageId);
            });
        }
        
        return messageId;
    };

    /**
     * Supprimer un message
     * @param {string} messageId - ID du message à supprimer
     */
    function removeMessage(messageId) {
        const messageElement = document.getElementById(messageId);
        if (messageElement) {
            messageElement.style.transform = 'translateX(100%)';
            messageElement.style.opacity = '0';
            
            setTimeout(() => {
                if (messageElement.parentNode) {
                    messageElement.parentNode.removeChild(messageElement);
                }
            }, 300);
        }
    }

    /**
     * Nettoyer tous les messages
     */
    window.clearAllMessages = function() {
        const messages = messagesContainer.querySelectorAll('.alert');
        messages.forEach(message => {
            if (message.id) {
                removeMessage(message.id);
            }
        });
    };

    /**
     * Messages de raccourci
     */
    window.showSuccess = function(message, duration = 5000) {
        return showFloatingMessage(message, 'success', duration);
    };

    window.showError = function(message, duration = 7000) {
        return showFloatingMessage(message, 'error', duration);
    };

    window.showWarning = function(message, duration = 6000) {
        return showFloatingMessage(message, 'warning', duration);
    };

    window.showInfo = function(message, duration = 5000) {
        return showFloatingMessage(message, 'info', duration);
    };

    // Gérer les messages existants (depuis Thymeleaf)
    const existingMessages = document.querySelectorAll('#floating-messages .alert');
    existingMessages.forEach((message, index) => {
        // Ajouter une animation d'entrée
        message.style.transform = 'translateX(100%)';
        message.style.opacity = '0';
        
        setTimeout(() => {
            message.style.transition = 'all 0.3s ease-out';
            message.style.transform = 'translateX(0)';
            message.style.opacity = '1';
        }, index * 100);
        
        // Auto-suppression pour les messages auto-dismiss
        if (message.classList.contains('auto-dismiss')) {
            setTimeout(() => {
                if (message.parentNode) {
                    message.style.transform = 'translateX(100%)';
                    message.style.opacity = '0';
                    setTimeout(() => {
                        if (message.parentNode) {
                            message.parentNode.removeChild(message);
                        }
                    }, 300);
                }
            }, 5000);
        }
    });

    // Gérer le redimensionnement de la fenêtre
    let resizeTimeout;
    window.addEventListener('resize', function() {
        clearTimeout(resizeTimeout);
        resizeTimeout = setTimeout(() => {
            // Repositionner le container si nécessaire
            if (window.innerWidth <= 768) {
                messagesContainer.style.left = '10px';
                messagesContainer.style.right = '10px';
                messagesContainer.style.maxWidth = 'none';
            } else {
                messagesContainer.style.left = '';
                messagesContainer.style.right = '20px';
                messagesContainer.style.maxWidth = '400px';
            }
        }, 250);
    });

    // Limiter le nombre de messages affichés simultanément
    const MAX_MESSAGES = 5;
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.type === 'childList') {
                const messages = messagesContainer.querySelectorAll('.alert');
                if (messages.length > MAX_MESSAGES) {
                    // Supprimer les plus anciens messages
                    for (let i = 0; i < messages.length - MAX_MESSAGES; i++) {
                        if (messages[i].id) {
                            removeMessage(messages[i].id);
                        }
                    }
                }
            }
        });
    });

    observer.observe(messagesContainer, {
        childList: true
    });

    console.log('✅ Floating Messages System - Ready');
});

/**
 * Fonction utilitaire pour les formulaires
 * Affiche un message de succès après soumission
 */
window.handleFormSuccess = function(message) {
    showSuccess(message || 'Opération réalisée avec succès !');
};

/**
 * Fonction utilitaire pour les erreurs de formulaire
 */
window.handleFormError = function(message) {
    showError(message || 'Une erreur est survenue. Veuillez réessayer.');
};

/**
 * Fonction pour confirmer une action avec message
 */
window.confirmAction = function(message, successCallback, cancelCallback) {
    if (confirm(message)) {
        if (typeof successCallback === 'function') {
            successCallback();
        }
        return true;
    } else {
        if (typeof cancelCallback === 'function') {
            cancelCallback();
        }
        return false;
    }
};