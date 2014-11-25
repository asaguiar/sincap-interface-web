$(document).ready(function () {
    $('#datMes').inputmask("mm/yyyy", {placeholder: "_"});

    $('.secao').hide();
    $('.ul').hide();

    $('.listas').click(function () {
        $(this).toggleClass("open");
        $(this).next().toggle();
    });

    $('.sub').click(function () {
        $(this).toggleClass("open");
        $(this).next().toggle();
    });
});

$(".select2").select2({
    placeholder: "Escolha as Instituições"
});

$("#rel-atividade-mensal").validate({
    rules: {
        'datMes': {
            required: true
        }
    },
    messages: {
        'datMes': {
            required: "Por favor, insira a data inicial."
        }
    },
    submitHandler: function (form) {
        form.submit();
    }
});



