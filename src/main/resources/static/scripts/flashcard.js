document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM fully loaded and parsed');

    const flashcards = document.querySelectorAll('.flashcard');
    console.log('Flashcards:', flashcards);

    const wordsToRepeat = JSON.parse(document.getElementById('wordsToRepeatData').textContent);
    console.log('Words to repeat:', wordsToRepeat);

    let currentIndex = parseInt(document.getElementById('currentIndex').value, 10);
    console.log('Current index:', currentIndex);

    flashcards.forEach(flashcard => {
        flashcard.addEventListener('click', function() {
            console.log('Flashcard clicked');

            currentIndex = (currentIndex + 1) % wordsToRepeat.length;
            document.getElementById('currentIndex').value = currentIndex;
            console.log('New current index:', currentIndex);

            const nextWord = wordsToRepeat[currentIndex];
            console.log('Next word:', nextWord);

            flashcard.querySelector('.flashcard-word').textContent = nextWord.word;
            flashcard.querySelector('.flashcard-translation').textContent = nextWord.translation;
        });
    });
});