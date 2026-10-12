const shareInput = document.querySelector('#client-share-link');
const copyLinkButton = document.querySelector('#copy-client-link');
const localNote = document.querySelector('#share-local-note');
const emailRow = document.querySelector('.email-share-row');
const emailLink = document.querySelector('#client-email-link');
const copyEmailButton = document.querySelector('#copy-email-text');

const copyText = async (text) => {
    try {
        await navigator.clipboard.writeText(text);
    } catch (error) {
        const temporaryField = document.createElement('textarea');
        temporaryField.value = text;
        temporaryField.style.position = 'fixed';
        temporaryField.style.opacity = '0';
        document.body.appendChild(temporaryField);
        temporaryField.select();
        document.execCommand('copy');
        temporaryField.remove();
    }
};

const showCopiedState = (button, originalLabel) => {
    button.textContent = 'Копирано';
    setTimeout(() => button.textContent = originalLabel, 1600);
};

if (shareInput && copyLinkButton) {
    shareInput.value = new URL(shareInput.dataset.path, window.location.origin).href;

    if (localNote && ['localhost', '127.0.0.1'].includes(window.location.hostname)) {
        localNote.hidden = false;
    }

    copyLinkButton.addEventListener('click', async () => {
        await copyText(shareInput.value);
        showCopiedState(copyLinkButton, 'Копирай линка');
    });

    if (emailRow && emailLink && copyEmailButton) {
        const subject = `Angebot ${emailRow.dataset.number}`;
        const body = [
            `Guten Tag ${emailRow.dataset.customer},`,
            '',
            `anbei erhalten Sie unser Angebot ${emailRow.dataset.number}.`,
            'Unter folgendem Link können Sie das Angebot ansehen, als PDF herunterladen und annehmen oder ablehnen:',
            '',
            shareInput.value,
            '',
            'Mit freundlichen Grüßen',
            emailRow.dataset.company
        ].join('\n');

        emailLink.href = `mailto:${encodeURIComponent(emailRow.dataset.email)}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
        copyEmailButton.addEventListener('click', async () => {
            await copyText(body);
            showCopiedState(copyEmailButton, 'Копирай текста');
        });
    }
}
