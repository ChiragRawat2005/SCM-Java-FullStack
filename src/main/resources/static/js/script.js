// --------------> Theme Button 

let currentTheme = getTheme();

//initial-->
document.addEventListener('DOMContentLoaded', () => {
    changeTheme();
    initPasswordToggle();
})

function changeTheme(){
    //set to web page
    document.querySelector('html').classList.add(currentTheme);

    const change_theme_button = document.querySelector('#theme_change_button')
    //text change
    change_theme_button.querySelector('span').textContent = currentTheme == "light" ? "Dark" : "Light";

    //change the theme
    change_theme_button.addEventListener('click', (event) => {
        const oldTheme = currentTheme;
        //set to light
        if(currentTheme == "dark"){
            currentTheme = "light";
        }else{
            //set to dark
            currentTheme = "dark";
        }
        
        //update in localstorage
        setTheme(currentTheme);
        document.querySelector('html').classList.remove(oldTheme);
        document.querySelector('html').classList.add(currentTheme);

        //text change
        change_theme_button.querySelector('span').textContent = currentTheme == "light" ? "Dark" : "Light";
    })
}

//set theme to localstorage
function setTheme(theme) {
    localStorage.setItem("theme", theme);
}

//get theme from localstorage
function getTheme() {
    let theme =  localStorage.getItem("theme")
    return theme ? theme : "light";
}

// --------------> Show Password

function initPasswordToggle() {
    const passwordInput = document.getElementById("password");
    const toggleBtn = document.getElementById("togglePasswordBtn");

    // page may not have password field (important)
    if (!passwordInput || !toggleBtn) return;

    toggleBtn.addEventListener("click", () => {
        passwordInput.type =
            passwordInput.type === "password" ? "text" : "password";
    });
}

// --------------> AJAX 

// ---------View Button---------
const viewContactModal = document.getElementById('default-modal');

// options with default values
const options = {
  placement: 'bottom-right',
  backdrop: 'dynamic',
  backdropClasses: 'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
  closable: true,
  onHide: () => {
      console.log('modal is hidden');
  },
  onShow: () => {
      console.log('modal is shown');
  },
  onToggle: () => {
      console.log('modal has been toggled');
  }
};

// instance option object
const instanceOptions = {
    id:'default-modal',
    override: true
};

const contactModal = new Modal(viewContactModal,options,instanceOptions);

function closeContactModal(){
    contactModal.hide();
}

async function loadContactdata(id){
    try {
        const response = await fetch(`http://localhost:8080/api/view_contacts/${id}`);
        const data = await response.json();

        // set text fields
        document.querySelector("#contact_name").innerHTML = data.name;
        document.querySelector("#contact_email").innerHTML = data.email;
        
        // When the address is empty
        document.querySelector("#contact_address").innerHTML =
            data.address && data.address.trim() !== "" ? data.address : "No Address Available";

        // When the message is empty
        document.querySelector("#contact_message").innerHTML =
            data.message && data.message.trim() !== "" ? data.message : "No Message Available";

        // set image
        const img = document.querySelector("#contact_picture");
        img.src = data.picture || "/images/default_user.png";

        // image error
        img.onerror = function () {
            this.src = "/images/default_user.png";
        };

    } catch (error) {
        console.log("Error:", error);
    }
}
 
// ---------Delete Button---------
let selectedContactId = null;

function openDeleteModal(contactId) {
    selectedContactId = contactId;
}

async function deleteContact() {
    if (!selectedContactId) return;

    try {
        const response = await fetch(
            `http://localhost:8080/api/delete_contacts/${selectedContactId}`,
            { method: "DELETE" }
        );

        if (response.ok) {

            // close modal
            document.querySelector('[data-modal-hide="popup-modal"]').click();

             // remove deleted row from table
            const row = document
                .querySelector(`button[onclick*="${selectedContactId}"]`)
                ?.closest("tr");

            if (row) row.remove();

            // show success message
            const msg = document.getElementById("successMessage");
            msg.classList.remove("hidden");
            
            // auto-hide after 6 seconds
            setTimeout(() => {
                msg.classList.add("hidden");
            }, 6000);

            setTimeout(() => {
                location.reload();
            }, 6000);

            selectedContactId = null;

        } else {
            alert("Failed to delete contact");
        }
    } catch (error) {
        console.error("Delete error:", error);
    }
}

// --------------> Image Size Validation 
function validateImageSize(input) {
const file = input.files[0];
const maxSize = 2 * 1024 * 1024; // 2MB

if (file && file.size > maxSize) {
    // show popup
    document.getElementById("imageSizePopup")
            .classList.remove("hidden");

    // reset file input
    input.value = "";
}
}

function closeImagePopup() {
    document.getElementById("imageSizePopup")
    .classList.add("hidden");
}

function validateImageType(input) {

    const file = input.files[0];
    if (!file) return true;

    const allowedTypes = ["image/jpeg", "image/png", "image/webp"];
    const allowedExtensions = [".jpg", ".jpeg", ".png", ".webp"];
    const fileName = file.name.toLowerCase();

    const validMime = allowedTypes.includes(file.type);
    const validExtension = allowedExtensions.some(ext => fileName.endsWith(ext));

    if (!(validMime || validExtension)) {

        document.getElementById("imageTypePopup")
                .classList.remove("hidden");

        input.dataset.invalid = "true";
        return false;
    }

    input.dataset.invalid = "false";
    return true;
}

function closeImageTypePopup() {
    document.getElementById("imageTypePopup")
            .classList.add("hidden");
}


// ================= PROFILE IMAGE PREVIEW (SAFE) =================
document.addEventListener("DOMContentLoaded", () => {

    const profileInput = document.getElementById("profilePicInput");
    const profilePreview = document.getElementById("profilePreview");

    // page-safe check
    if (!profileInput || !profilePreview) return;

    profileInput.addEventListener("change", function () {

        const file = this.files[0];
        if (!file) return;

        // SAME limit as validateImageSize (do NOT change original function)
        const maxSize = 2 * 1024 * 1024;

        // if file was reset by validateImageSize → skip
        if (file.size > maxSize) return;

        const reader = new FileReader();
        reader.onload = function (e) {
            profilePreview.src = e.target.result;
        };
        reader.readAsDataURL(file);
    });
});
