// CampusGuard Interactive Functions
document.addEventListener("DOMContentLoaded", function () {
    fetchUnreadNotificationCount();

    // Auto-dismiss alerts after 5 seconds
    setTimeout(function () {
        const alerts = document.querySelectorAll('.alert-dismissible');
        alerts.forEach(alert => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
});

function fetchUnreadNotificationCount() {
    const badge = document.getElementById("unreadNotificationBadge");
    if (!badge) return;

    fetch("/api/notifications/unread-count")
        .then(res => res.ok ? res.json() : null)
        .then(data => {
            if (data && data.count > 0) {
                badge.innerText = data.count;
                badge.style.display = "inline-block";
            } else {
                badge.style.display = "none";
            }
        })
        .catch(() => {});
}

// Quick Viva Demo Account Filler
function fillDemoLogin(role) {
    const emailField = document.getElementById("email");
    const passField = document.getElementById("password");

    if (!emailField || !passField) return;

    if (role === 'admin') {
        emailField.value = "admin@college.test";
        passField.value = "password123";
    } else if (role === 'security') {
        emailField.value = "security@college.test";
        passField.value = "password123";
    } else if (role === 'student') {
        emailField.value = "student1@college.test";
        passField.value = "password123";
    }
}
