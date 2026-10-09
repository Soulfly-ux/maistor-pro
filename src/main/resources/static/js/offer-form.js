const container = document.querySelector('#line-items');
const addButton = document.querySelector('#add-item');

if (container && addButton) {
    const reindex = () => {
        [...container.querySelectorAll('.line-item')].forEach((row, index) => {
            row.querySelectorAll('input').forEach((input) => {
                input.name = input.name.replace(/items\[\d+]/, `items[${index}]`);
                input.id = input.id.replace(/items\d+/, `items${index}`);
            });
        });
    };

    const updateRemoveButtons = () => {
        const buttons = container.querySelectorAll('.remove-item');
        buttons.forEach((button) => {
            button.disabled = buttons.length === 1;
        });
    };

    container.addEventListener('click', (event) => {
        if (!event.target.matches('.remove-item')) return;
        event.target.closest('.line-item').remove();
        reindex();
        updateRemoveButtons();
    });

    addButton.addEventListener('click', () => {
        const template = container.querySelector('.line-item');
        const newRow = template.cloneNode(true);
        newRow.querySelectorAll('input').forEach((input) => {
            input.value = input.name.endsWith('.quantity') ? '1' :
                input.name.endsWith('.unit') ? 'Stk.' : '';
        });
        newRow.querySelectorAll('.field-error').forEach((error) => error.remove());
        container.appendChild(newRow);
        reindex();
        updateRemoveButtons();
        newRow.querySelector('input').focus();
    });

    updateRemoveButtons();
}
