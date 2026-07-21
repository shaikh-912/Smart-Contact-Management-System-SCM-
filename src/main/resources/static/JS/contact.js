console.log("contact.js loaded");

const DEFAULT_AVATAR =
    "https://as1.ftcdn.net/jpg/07/24/59/76/1000_F_724597608_pmo5BsVumFcFyHJKlASG2Y2KpkkfiYUU.webp";

function openContactModal(button) {

    // Get values from button data attributes
    const name = button.dataset.name;
    const email = button.dataset.email;
    const phone = button.dataset.phone;
    const picture = button.dataset.picture;
    const website = button.dataset.website;
    const linkedin = button.dataset.linkedin;
    const favorite = button.dataset.favorite === "true";

    // Image
    const image = document.getElementById("modalImage");
    image.src = picture && picture !== "" ? picture : DEFAULT_AVATAR;
    image.onerror = () => { image.src = DEFAULT_AVATAR; };

    // Name + subtitle email
    document.getElementById("modalName").textContent = name || "Unnamed Contact";
    document.getElementById("modalEmailSubtitle").textContent = email || "";

    // Contact info card values
    document.getElementById("modalPhone").textContent = phone || "Not Available";
    document.getElementById("modalEmail").textContent = email || "Not Available";

    // Website
    const websiteLink = document.getElementById("modalWebsite");
    if (website && website !== "") {
        websiteLink.href = website;
        websiteLink.textContent = website;
    } else {
        websiteLink.removeAttribute("href");
        websiteLink.textContent = "Not Available";
    }

    // LinkedIn
    const linkedinLink = document.getElementById("modalLinkedIn");
    if (linkedin && linkedin !== "") {
        linkedinLink.href = linkedin;
        linkedinLink.textContent = "View Profile";
    } else {
        linkedinLink.removeAttribute("href");
        linkedinLink.textContent = "Not Available";
    }

    // Favorite badge
    const favoriteBadge = document.getElementById("modalFavorite");
    if (favorite) {
        favoriteBadge.className =
            "inline-flex items-center gap-1.5 mt-3 px-4 py-1.5 rounded-full text-xs sm:text-sm font-semibold bg-yellow-100 text-yellow-700";
        favoriteBadge.innerHTML = '<i class="fa-solid fa-star text-xs"></i> Favorite Contact';
    } else {
        favoriteBadge.className =
            "inline-flex items-center gap-1.5 mt-3 px-4 py-1.5 rounded-full text-xs sm:text-sm font-semibold bg-gray-100 text-gray-500";
        favoriteBadge.innerHTML = '<i class="fa-regular fa-star text-xs"></i> Normal Contact';
    }

    // Show modal
    const modal = document.getElementById("view-contact-modal");
    modal.classList.remove("hidden");
}

function closeContactModal() {
    document.getElementById("view-contact-modal").classList.add("hidden");
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("closeModalTop")?.addEventListener("click", closeContactModal);
    document.getElementById("closeModalBottom")?.addEventListener("click", closeContactModal);

    // Close when clicking the overlay itself (not the modal card)
    document.getElementById("view-contact-modal")?.addEventListener("click", (e) => {
        if (e.target.id === "view-contact-modal") closeContactModal();
    });

    // Close on Escape key
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") closeContactModal();
    });
});

const baseUrl="/user/contacts";

//delete Contact
async function deleteContact(id){
	Swal.fire({
	  title: "Do you want to Delete the Contact?",
	  icon:"warning",
	  showCancelButton: true,
	  confirmButtonText: "Delete",
	}).then((result) => {
	  /* Read more about isConfirmed, isDenied below */
	  if (result.isConfirmed) {
		const url=`${baseUrl}/delete/`+ id;
		Swal.fire("Deleted!", "", "success");
		window.location.replace(url);
		}
	});
}





