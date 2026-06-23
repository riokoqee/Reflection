const header = document.querySelector("[data-header]");
const parallax = document.querySelector("[data-parallax]");
const revealBlocks = document.querySelectorAll(".reveal");
const faq = document.querySelector("[data-faq]");
const faqToggle = document.querySelector("[data-faq-toggle]");
const faqPanel = document.querySelector("[data-faq-panel]");
const faqClose = document.querySelector("[data-faq-close]");

function syncHeader() {
  header?.classList.toggle("is-scrolled", window.scrollY > 12);
}

syncHeader();
window.addEventListener("scroll", syncHeader, { passive: true });

if ("IntersectionObserver" in window) {
  const observer = new IntersectionObserver((entries) => {
    for (const entry of entries) {
      if (entry.isIntersecting) {
        entry.target.classList.add("is-visible");
        observer.unobserve(entry.target);
      }
    }
  }, { threshold: 0.16 });

  revealBlocks.forEach((block) => observer.observe(block));
}
else {
  revealBlocks.forEach((block) => block.classList.add("is-visible"));
}

const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)");

window.addEventListener("pointermove", (event) => {
  if (!parallax || reduceMotion.matches) {
    return;
  }

  const x = (event.clientX / window.innerWidth - 0.5) * 10;
  const y = (event.clientY / window.innerHeight - 0.5) * 6;
  parallax.style.transform = `scale(1.02) translate(${x}px, ${y}px)`;
}, { passive: true });

function setFaqOpen(open) {
  if (!faqToggle || !faqPanel) {
    return;
  }

  faqToggle.setAttribute("aria-expanded", String(open));
  faqPanel.hidden = !open;
}

faqToggle?.addEventListener("click", () => {
  setFaqOpen(faqPanel?.hidden ?? true);
});

faqClose?.addEventListener("click", () => {
  setFaqOpen(false);
  faqToggle?.focus();
});

document.addEventListener("keydown", (event) => {
  if (event.key === "Escape") {
    setFaqOpen(false);
  }
});

document.addEventListener("pointerdown", (event) => {
  if (!faq || faqPanel?.hidden) {
    return;
  }
  if (!faq.contains(event.target)) {
    setFaqOpen(false);
  }
});
