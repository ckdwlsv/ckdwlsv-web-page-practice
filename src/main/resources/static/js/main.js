document.addEventListener("DOMContentLoaded", () => {

    const menus = document.querySelectorAll(".menu-hover");

    menus.forEach(el => {
        el.addEventListener("mouseenter", () => {
            el.style.transform = "scale(1.1)";
        });

        el.addEventListener("mouseleave", () => {
            el.style.transform = "scale(1)";
        });
    });

});

document.addEventListener("DOMContentLoaded", () => {

    const boardLink = document.querySelector(".board-link");

    if (boardLink) {
        boardLink.style.display = "block";
        boardLink.style.textAlign = "center";
        boardLink.style.margin = "30px auto";
        boardLink.style.width = "fit-content";
    }

});
