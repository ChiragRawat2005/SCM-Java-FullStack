/* =========================================================
   EXPORT CONTACTS – JS ONLY (PERSIST SELECTION ACROSS PAGES)
   ========================================================= */

const STORAGE_KEY = "export_contacts_full";

/* ================= STORAGE HELPERS ================= */

function getStore() {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) || {};
}

function setStore(data) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
}

/* ================= ROW SELECTION ================= */

function handleRowSelection(cb) {

    let store = getStore();
    const id = cb.value;

    if (cb.checked) {
        store[id] = {
            picture: cb.dataset.picture || "",
            name: cb.dataset.name || "",
            email: cb.dataset.email || "",
            phone: cb.dataset.phone || "",
            address: cb.dataset.address || "",
            message: cb.dataset.message || "",
            favourite: cb.dataset.fav === "true" ? "TRUE" : "FALSE",
            website: cb.dataset.website || "",
            linkedin: cb.dataset.linkedin || ""
        };
    } else {
        delete store[id];
    }

    setStore(store);
}

/* ================= SELECT ALL (CURRENT PAGE) ================= */

function toggleSelectAll(master) {

    document.querySelectorAll(".rowCheckbox").forEach(cb => {
        cb.checked = master.checked;
        handleRowSelection(cb);
    });
}

/* ================= RESTORE SELECTION ON PAGE LOAD ================= */

document.addEventListener("DOMContentLoaded", () => {

    const store = getStore();

    document.querySelectorAll(".rowCheckbox").forEach(cb => {
        if (store[cb.value]) {
            cb.checked = true;
        }
    });
});

/* ================= EXPORT SELECTED CONTACTS ================= */

function exportSelectedContacts() {

    const store = getStore();
    const ids = Object.keys(store);

    if (ids.length === 0) {
        showNoSelectionCard();
        return;
    }

    const table = document.createElement("table");

    table.innerHTML = `
        <thead>
            <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Address</th>
                <th>Message</th>
                <th>Favourite</th>
                <th>Website</th>
                <th>LinkedIn</th>
            </tr>
        </thead>
        <tbody></tbody>
    `;

    const tbody = table.querySelector("tbody");

    ids.forEach(id => {
        const c = store[id];

        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${c.name}</td>
            <td>${c.email}</td>
            <td>${c.phone}</td>
            <td>${c.address}</td>
            <td>${c.message}</td>
            <td>${c.favourite}</td>
            <td>${c.website}</td>
            <td>${c.linkedin}</td>
        `;

        tbody.appendChild(tr);
    });

    document.body.appendChild(table);

    TableToExcel.convert(table, {
        name: "contacts.xlsx",
        sheet: { name: "Contacts" }
    });

    table.remove();
}

/* ================= EXPORT ALL CONTACTS ================= */

function exportAllContacts() {

    // mark intent in sessionStorage
    sessionStorage.setItem("EXPORT_ALL", "true");

    // load ALL contacts in one page
    // size should be safely larger than max contacts
    window.location.href = "/exportcontacts?page=0&size=100000";
}

/* ================= AUTO SELECT & EXPORT ================= */

document.addEventListener("DOMContentLoaded", () => {

    const exportAll = sessionStorage.getItem("EXPORT_ALL");

    if (exportAll === "true") {

        // select all checkboxes on page
        document.querySelectorAll(".rowCheckbox").forEach(cb => {
            cb.checked = true;
        });

        // small delay to ensure DOM is ready
        setTimeout(() => {
            exportSelectedContacts();

            // cleanup
            sessionStorage.removeItem("EXPORT_ALL");
        }, 300);
    }
});

function exportAllContacts() {
    window.location.href = "/exportcontacts/download";
}


/* ================= RESET (REFRESH ICON) ================= */

function resetExportSelection() {
    localStorage.removeItem(STORAGE_KEY);
    location.reload();
}

/* ================= NO SELECTION CARD ================= */

function showNoSelectionCard() {

    if (document.getElementById("noSelectionCard")) return;

    const card = document.createElement("div");
    card.id = "noSelectionCard";
    card.className = "fixed inset-0 z-[9999] flex items-center justify-center bg-black/50";

    card.innerHTML = `
        <div class="bg-white dark:bg-gray-800 rounded-xl p-6 w-full max-w-sm text-center shadow-xl">
            <i class="fa-solid fa-circle-exclamation text-4xl text-red-500 mb-4"></i>
            <h2 class="text-xl font-bold mb-2">No Contact Selected</h2>
            <p class="text-gray-500 mb-6">
                Please select at least one contact to export.
            </p>
            <button onclick="closeNoSelectionCard()"
                class="px-6 py-2 bg-blue-600 text-white rounded-lg">
                OK
            </button>
        </div>
    `;

    document.body.appendChild(card);
}

function closeNoSelectionCard() {
    document.getElementById("noSelectionCard")?.remove();
}

/* ================= INFO MODAL ================= */

function openExportInfoModal(btn) {

    const modal = document.getElementById("export-info-modal");
    if (!modal) {
        console.error("export-info-modal not found");
        return;
    }

    const id = btn.dataset.id;
    const store = getStore();
    let c = store[id];

    if (!c) {
        const row = btn.closest("tr");
        if (!row) return;

        c = {
            picture: row.querySelector("img")?.src || "",
            name: row.querySelector(".export-name")?.innerText || "N/A",
            email: row.querySelector(".export-email")?.innerText || "N/A",
            phone: row.querySelector(".export-phone")?.innerText || "N/A",
            address: row.querySelector(".export-address")?.innerText || "N/A",
            message: row.querySelector(".export-message")?.innerText || "N/A",
            favourite: row.querySelector(".export-fav")?.innerText || "FALSE",
            website: row.querySelector(".export-website")?.innerText || "",
            linkedin: row.querySelector(".export-linkedin")?.innerText || ""
        };
    }

    modal.querySelector("#info_contact_name").innerText = c.name;
    modal.querySelector("#info_contact_email").innerText = c.email;
    modal.querySelector("#info_contact_phone").innerText = c.phone;
    modal.querySelector("#info_contact_address").innerText = c.address;
    modal.querySelector("#info_contact_message").innerText = c.message;
    modal.querySelector("#info_contact_favourite").innerText = c.favourite;

    modal.querySelector("#info_contact_favourite").innerText = c.favourite;

/* ========= WEBSITE ========= */
const websiteEl = modal.querySelector("#info_contact_website");
if (c.website && c.website.trim() !== "") {
    websiteEl.href = c.website.startsWith("http")
        ? c.website
        : "https://" + c.website;
    websiteEl.innerText = c.website;
    websiteEl.classList.remove("hidden");
} else {
    websiteEl.classList.add("hidden");
}

/* ========= LINKEDIN ========= */
const linkedinEl = modal.querySelector("#info_contact_linkedin");
if (c.linkedin && c.linkedin.trim() !== "") {
    linkedinEl.href = c.linkedin.startsWith("http")
        ? c.linkedin
        : "https://" + c.linkedin;
    linkedinEl.innerText = c.linkedin;
    linkedinEl.classList.remove("hidden");
} else {
    linkedinEl.classList.add("hidden");
}


    const img = modal.querySelector("#info_contact_picture");
    img.src = c.picture?.trim() ? c.picture : "/images/default_contact.png";

    modal.classList.remove("hidden");
}


function closeExportInfoModal() {
    const modal = document.getElementById("export-info-modal");
    if (!modal) return;

    modal.classList.add("hidden");
    document.body.classList.remove("overflow-hidden");
}

