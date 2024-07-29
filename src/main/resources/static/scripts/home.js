document.addEventListener('DOMContentLoaded', function() {
    const flashcards = document.querySelectorAll('.flashcard');
    flashcards.forEach(flashcard => {
        flashcard.addEventListener('click', function() {
            const word = flashcard.querySelector('.flashcard-word').textContent;
            const translation = flashcard.querySelector('.flashcard-translation').textContent;
            checkAnswer(word, translation);
            reloadFlashcard(flashcard);
            flashcard.classList.add('flashcard-pulsing');
            setTimeout(() => {
                flashcard.classList.remove('flashcard-pulsing');
            }, 1500);
        });
    });

    // Get the modal
    const modal = document.getElementById("popupModal");
    const span = document.getElementsByClassName("close")[0];

    // When the user clicks on <span> (x), close the modal
    span.onclick = function() {
        modal.style.display = "none";
        confetti.stop();
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
            confetti.stop();
        }
    }
});

function checkAnswer(word, translation) {
    fetch(`/checkAnswer/${word}/${translation}`)
        .then(response => response.json())
        .then(correctWordOnView => {
            if (correctWordOnView === 8) {
                showPopup('Congratulations! You are doing a great job. Now all flashcards are correct. Restarting...');
                setTimeout(() => {
                    location.reload();
                }, 3000);
                const start = () => {
                   confetti.start();
                };
            }
        })
        .catch(error => console.error('Error:', error));
}

function reloadFlashcard(flashcard) {
    fetch(`/reloadFlashcard`)
        .then(response => response.json())
        .then(data => {
            flashcard.querySelector('.flashcard-word').textContent = data.word;
            flashcard.querySelector('.flashcard-translation').textContent = data.translation;
        })
        .catch(error => console.error('Error:', error));
}

function showPopup(message) {
    const modal = document.getElementById("popupModal");
    const popupMessage = document.getElementById("popupMessage");
    popupMessage.textContent = message;
    modal.style.display = "block";
    confetti.start();
}