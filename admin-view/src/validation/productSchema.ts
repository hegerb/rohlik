import * as yup from 'yup';

export const productSchema = yup.object({
    name: yup
        .string()
        .required('Název produktu je povinný')
        .min(3, 'Název musí mít alespoň 3 znaky')
        .max(100, 'Název může mít maximálně 100 znaků'),
    price: yup
        .number()
        .required('Cena je povinná')
        .positive('Cena musí být kladné číslo')
        .max(999999.99, 'Cena je příliš vysoká'),
    stockQuantity: yup
        .number()
        .required('Skladové množství je povinné')
        .min(0, 'Skladové množství nemůže být záporné')
        .integer('Skladové množství musí být celé číslo')
}); 