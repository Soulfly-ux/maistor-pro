const container = document.querySelector('#line-items');
const addButton = document.querySelector('#add-item');

if (container && addButton) {
    const createRow = (values = {}) => {
        const template = container.querySelector('.line-item');
        const firstDescription = template.querySelector('input[name$=".description"]');
        const useEmptyFirstRow = container.children.length === 1 && !firstDescription.value.trim();
        const newRow = useEmptyFirstRow ? template : template.cloneNode(true);

        newRow.querySelectorAll('input').forEach((input) => {
            const fieldName = input.name.split('.').pop();
            input.value = values[fieldName] ?? (fieldName === 'quantity' ? '1' : fieldName === 'unit' ? 'Stk.' : '');
        });
        newRow.querySelectorAll('.field-error').forEach((error) => error.remove());

        if (!useEmptyFirstRow) container.appendChild(newRow);
        reindex();
        updateRemoveButtons();
        newRow.querySelector('input').focus();
    };

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
        createRow();
    });

    document.querySelectorAll('.service-preset').forEach((button) => {
        button.addEventListener('click', () => {
            createRow({
                description: button.dataset.description,
                quantity: '1',
                unit: button.dataset.unit,
                unitPrice: button.dataset.price
            });
        });
    });

    updateRemoveButtons();
}
