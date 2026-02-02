document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector('form[enctype="multipart/form-data"]');
    if (!form) return;

    form.addEventListener("submit", function (e) {

        const fileInput = document.getElementById("profilePicInput");

        if (fileInput && fileInput.dataset.invalid === "true") {
            e.preventDefault();
            return false;
        }
    });
});

document.addEventListener("DOMContentLoaded", () => {

    const contactImageInput = document.getElementById("picture");
    const contactPreview = document.getElementById("upload_image_preview");

    if (!contactImageInput || !contactPreview) return;

    contactImageInput.addEventListener("change", function () {

        const file = this.files[0];
        if (!file) return;

        // same 2MB rule (consistent with validateImageSize)
        const maxSize = 2 * 1024 * 1024;
        if (file.size > maxSize) return;

        const reader = new FileReader();
        reader.onload = function (e) {
            contactPreview.src = e.target.result;
        };
        reader.readAsDataURL(file);
    });
});
