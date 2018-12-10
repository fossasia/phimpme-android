// Autohide feature for navigation bar
     var prevScrollpos = window.pageYOffset;
        $(window).scroll(function(){
            var currentScrollPos = window.pageYOffset;
        if ( currentScrollPos < 780) {
            $('nav').css('top', '0px');
        } else if(currentScrollPos - prevScrollpos > 3){
            setTimeout(function(){
                $('nav').css('top', '-100px');
            },400)
        } else if(prevScrollpos - currentScrollPos > 3){
            setTimeout(function(){
                $('nav').css('top', '0px');
            }, 400);
        }
        prevScrollpos = currentScrollPos ;
        $('nav').clearQueue();
    });