$(document).ready(function () {


    //highlight current / active link
    $('ul.main-menu li a').each(function () {
        if ($($(this))[0].href == String(window.location))
            $(this).parent().addClass('active');
    });

    // others
    docReady();
});


function docReady() {

    // box的close，minimize，setting按钮，setting就不要了吧。。。
    $('.btn-close').click(function (e) {
        e.preventDefault();
        $(this).parent().parent().parent().fadeOut();
    });
    $('.btn-minimize').click(function (e) {
        e.preventDefault();
        var $target = $(this).parent().parent().next('.box-content');
        if ($target.is(':visible')) $('i', $(this)).removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
        else                       $('i', $(this)).removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
        $target.slideToggle();
    });

    $('.setting').click(function (e) {
        e.preventDefault();
        $('#myModal').modal('show');
    });

}

//显示下拉navbar
// function showhid(id){
//         document.getElementById(id).style.display ='block';
// }
// function showhid2(id){
//     document.getElementById(id).style.display ='none';
// }
