//
// do not edit this file for much easy update
// place custom function to other js file
// --------------------------------------------------

var $ = jQuery.noConflict();

(function($) {
  'use strict';

//
// global variable
// --------------------------------------------------

  var $html = $('html'),
      $body = $('body'),
      easingCb = [0.770, 0.000, 0.175, 1.000], // cubic-bezier(0.770, 0.000, 0.175, 1.000); easeInOutQuart
      isMobile,
      isDesktop,
      isIE9;

//
// function start
// --------------------------------------------------

  var BONEFISHCODE = {
    ready: function() {
      BONEFISHCODE.fn_viewportFix();
      BONEFISHCODE.fn_deviceDetect();
      BONEFISHCODE.fn_scrollspy();
      BONEFISHCODE.fn_smoothScroll();
      BONEFISHCODE.fn_contactForm();
      BONEFISHCODE.fn_subscribeForm();
      BONEFISHCODE.fn_countTo();
      BONEFISHCODE.fn_siteBg();
      BONEFISHCODE.fn_siteBgAudio();
      BONEFISHCODE.fn_siteBgAnimation();
      BONEFISHCODE.fn_parallaxEffect();
      BONEFISHCODE.fn_overlay();
    },
    scroll: function() {
      BONEFISHCODE.fn_isScroll();
    },
    resize: function() {
      BONEFISHCODE.fn_scrollspy();
      BONEFISHCODE.fn_smoothScroll();
    },
    load: function() {
      BONEFISHCODE.fn_siteLoader();
      BONEFISHCODE.fn_isScroll();
    },

//
// ie10 viewport fix
// --------------------------------------------------

    fn_viewportFix: function() {
      if (navigator.userAgent.match(/IEMobile\/10\.0/)) {
        var msViewportStyle = document.createElement('style');
        msViewportStyle.appendChild(
          document.createTextNode(
            '@-ms-viewport{width:auto!important}'
          )
        );
        document.querySelector('head').appendChild(msViewportStyle);
      }
    },

//
// device detect
// --------------------------------------------------

    fn_deviceDetect: function() {
      if ($html.hasClass('desktop')) {
        $html.addClass('is-desktop');
        isMobile = false;
        isDesktop = true;
      } else {
        $html.addClass('is-mobile');
        isMobile = true;
        isDesktop = false;
      }
      if ($html.hasClass('ie9')) {
        isIE9 = true;
      }
    },

//
// site loader
// --------------------------------------------------

    fn_siteLoader: function() {
      var $siteLoader = $('.site-loader');
      var $siteMain = $('.site-main');

      $siteLoader.velocity({
        translateZ: 0,
        translateY: ['-100%', 0]
      }, {
        delay: 500,
        duration: 1500,
        easing: easingCb,
        complete: function() {
          $body.addClass('is-loaded');
          $(this).remove();
          BONEFISHCODE.fn_scrollReveal();
        }
      });

      $siteMain.velocity({
        translateZ: 0,
        translateY: [0, 300]
      }, {
        delay: 500,
        duration: 1500,
        easing: easingCb
      });
    },

//
// scrollspy
// --------------------------------------------------

    fn_scrollspy: function() {
      var target = '.navbar';

      $body.scrollspy({
        target: target,
        offset: parseInt($(target).height())
      });
    },

//
// smooth scroll
// --------------------------------------------------

    fn_smoothScroll: function() {
      var offset = parseInt($('.navbar').height());

      $('a[href^=#]').not('.modal-open').on('click', function(e) {
        var $target = $($(this).attr('href'));

        e.preventDefault();

        if ($target.length) {
          $target.velocity('stop').velocity('scroll', {
            duration: 1500,
            easing: easingCb,
            offset: -offset
          });
        }
      });
    },

//
// is scroll
// --------------------------------------------------

    fn_isScroll: function() {
      var scroll = $(window).scrollTop();

      if (scroll > 0) {
        $body.addClass('is-scroll');
      } else {
        $body.removeClass('is-scroll');
      }
    },

//
// contact form
// --------------------------------------------------
//

  fn_contactForm: function() {
    var $form = $('#contactForm');
    var $formNotify = $form.find('.form-notify');

    $form.validate({
      onclick: false,
      onfocusout: false,
      onkeyup: false,
      rules: {
        name: {
          required: true
        },
        email: {
          required: true,
          email: true
        },
        message: {
          required: true
        }
      },
      errorPlacement: function(error, element) {},
      highlight: function(element) {
        $(element).parent('.form-group').addClass('error');
      },
      unhighlight: function(element) {
        $(element).parent('.form-group').removeClass('error');
      },
      submitHandler: function(form) {
        $.ajax({
          type: 'POST',
          dataType: 'json',
          url: 'assets/php/contact.php',
          cache: false,
          data: $form.serialize(),
          success: function(data) {
            if (data.status != 'success') {
              $formNotify.html(data.msg).show();
            } else {
              $form.validate().resetForm();
              $form[0].reset();
              $form.find('.error').removeClass('error');
              $form.find('button').blur();
              $formNotify.html(data.msg).show();
            }
          },
          error: function(XMLHttpRequest, textStatus, errorThrown) {
            $formNotify.html('<i class="fa fa-warning"></i> An error occurred. Please try again later.').show();
            //$formNotify.html(XMLHttpRequest.responseText);
          },
        });
      },
      invalidHandler: function(event, validator) {
        var errors = validator.numberOfInvalids();

        if (errors) {
          var message = errors == 1 ?
          '<i class="fa fa-warning"></i>You missed 1 field. It has been highlighted.' :
          '<i class="fa fa-warning"></i>You missed ' + errors + ' fields. They have been highlighted.';
          $formNotify.html(message).show();
        }
      }
    });
  },

//
// subscribe form
// --------------------------------------------------
//

  fn_subscribeForm: function() {
    var $form = $('#subscribeForm');
    var $formNotify = $form.find('.form-notify');

    $form.validate({
      onclick: false,
      onfocusout: false,
      onkeyup: false,
      rules: {
        email: {
          required: true,
          email: true
        }
      },
      errorPlacement: function(error, element) {},
      highlight: function(element) {
        $(element).parent('.form-group').addClass('error');
      },
      unhighlight: function(element) {
        $(element).parent('.form-group').removeClass('error');
      },
      submitHandler: function(form) {
        $.ajax({
          type: 'POST',
          dataType: 'json',
          url: 'assets/php/subscribe.php',
          cache: false,
          data: $form.serialize(),
          success: function(data) {
            if (data.status != 'success') {
              $formNotify.html(data.msg).show();
            } else {
              $form.validate().resetForm();
              $form[0].reset();
              $form.find('.error').removeClass('error');
              $form.find('button').blur();
              $formNotify.html(data.msg).show();
            }
          },
          error: function(XMLHttpRequest, textStatus, errorThrown) {
            $formNotify.html('<i class="fa fa-warning"></i> An error occurred. Please try again later.').show();
            //$formNotify.html(XMLHttpRequest.responseText);
          },
        });
      },
      invalidHandler: function(event, validator) {
        var errors = validator.numberOfInvalids();

        if (errors) {
          var message = errors == 1 ?
          '<i class="fa fa-warning"></i>You missed 1 field. It has been highlighted.' :
          '<i class="fa fa-warning"></i>You missed ' + errors + ' fields. They have been highlighted.';
          $formNotify.html(message).show();
        }
      }
    });
  },

//
// count to
// --------------------------------------------------

  fn_countTo: function() {
    var $countNum = $('.count-num');

    if ($countNum.length) {
      $countNum.appear(function() {
        var $this = $(this);

        $this.countTo({
          from: 0,
          to: $this.text(),
          speed: 1000,
          refreshInterval: 20, // default 100
        });
      });
    }
  },

//
// site background
// --------------------------------------------------

  fn_siteBg: function() {
    // mobile
    if (isMobile) {
      if (_bg_style_mobile === 0 || _bg_style_mobile == 1) {
        $body.addClass('site-bg-color');
      }

      if (_bg_style_mobile == 2 || _bg_style_mobile == 3) {
        BONEFISHCODE.fn_siteBgImg();
      }
      else if (_bg_style_mobile == 4 || _bg_style_mobile == 5 || _bg_style_mobile == 6 || _bg_style_mobile == 7) {
        $(window).on('load', function() {
          BONEFISHCODE.fn_siteBgSlideshow();
        });
      }
    }

    // desktop
    else {
      if (_bg_style_desktop === 0 || _bg_style_desktop == 1) {
        $body.addClass('site-bg-color');
      }
      if (_bg_style_desktop == 2 || _bg_style_desktop == 3) {
        BONEFISHCODE.fn_siteBgImg();
      }
      else if (_bg_style_desktop == 4 || _bg_style_desktop == 5 || _bg_style_desktop == 6 || _bg_style_desktop == 7) {
        BONEFISHCODE.fn_siteBgSlideshow();
      }
      else if (_bg_style_desktop == 8 || _bg_style_desktop == 9 || _bg_style_desktop == 10) {
        BONEFISHCODE.fn_siteBgVideo();
      }
      else if (_bg_style_desktop == 11 || _bg_style_desktop == 12 || _bg_style_desktop == 13) {
        BONEFISHCODE.fn_siteBgVideoYoutube();
      }
    }
  },

//
// overlay
// --------------------------------------------------

  fn_overlay: function() {
    var $overlay = $('.site-bg-overlay');

    if (_site_bg_overlay_disable) {
      $overlay.remove();
    }
    if (_bg_style_desktop !== 0 && _bg_style_desktop !== 1) {
      $('.is-desktop').find($body).addClass('is-site-bg-overlay');
      $('.is-desktop').find($overlay).show().css('background-color', _site_bg_overlay_color);
    }
    if (_bg_style_mobile !== 0 && _bg_style_mobile !== 1) {
      $('.is-mobile').find($body).addClass('is-site-bg-overlay');
      $('.is-mobile').find($overlay).show().css('background-color', _site_bg_overlay_color);
    }
  },

//
// image background
// --------------------------------------------------

  fn_siteBgImg: function() {
    $body.addClass('is-site-bg-img');
    $('.site-bg-video').remove();
  },

//
// slideshow background
// --------------------------------------------------

  fn_siteBgSlideshow: function() {
    var $siteBgImg = $('.site-bg-img');

    $('.site-bg-video').remove();

    $body.addClass('is-site-bg-slideshow');
    for (var i = 1; i <= _bg_slideshow_image_amount; i++) {
      $siteBgImg.append('<img src="assets/img/bg/site-bg-slideshow-' + (i < 10 ? '0' + i : i) + '.jpg">');
    }

    if (isMobile) {
      if (_bg_style_mobile == 4 || _bg_style_mobile == 5) {
        fn_ss();
      } else if (_bg_style_mobile == 6 || _bg_style_mobile == 7) {
        fn_kenburnsy();
      }
    }
    else {
      if (_bg_style_desktop == 4 || _bg_style_desktop == 5) {
        fn_ss();
      } else if (_bg_style_desktop == 6 || _bg_style_desktop == 7) {
        fn_kenburnsy();
      }
    }

    function fn_ss() {
      $siteBgImg.ss({
        fullscreen: true,
        duration: _bg_slideshow_duration,
        fadeInDuration: 1500
      });
    }

    function fn_kenburnsy() {
      $siteBgImg.kenburnsy({
        fullscreen: true,
        duration: _bg_slideshow_duration,
        fadeInDuration: 1500
      });
    }
  },

//
// html5 video background
// --------------------------------------------------

  fn_siteBgVideo: function() {
    var $video = $('.site-bg-video');
    var $audio = $('.audio-toggle');

    $body.addClass('is-site-bg-video');

    $video.append('<video id="bgVideo" autoplay loop>' +
                  '<source src="assets/video/video.mp4" type="video/mp4">' +
                  '</video>');

    var bgVideo = document.getElementById('bgVideo');

    if (_bg_style_desktop == 8) {
      bgVideo.muted = true;
      $audio.remove();
    } else if (_bg_style_desktop == 9) {
      $body.addClass('is-audio-on');

      $audio.on('click', function() {
        if ($body.hasClass('is-audio-on')) {
          bgVideo.muted = true;
          $body.removeClass('is-audio-on').addClass('is-audio-off');
        } else if ($body.hasClass('is-audio-off')) {
          bgVideo.muted = false;
          $body.removeClass('is-audio-off').addClass('is-audio-on');
        }
      });
    }
  },

//
// youtube video background
// --------------------------------------------------

  fn_siteBgVideoYoutube: function() {
    var $video = $('.site-bg-video');
    var $audio = $('.audio-toggle');

    $body.addClass('is-site-bg-video-youtube');
    if (_bg_style_desktop == 11 || _bg_style_desktop == 13) {
      $video.attr('data-property', '{videoURL: _bg_video_youtube_url, autoPlay: true, loop: _bg_video_youtube_loop, startAt: _bg_video_youtube_start, stopAt: _bg_video_youtube_end, mute: true, quality: _bg_video_youtube_quality, realfullscreen: true, optimizeDisplay: true, addRaster: false, showYTLogo: false, showControls: false, stopMovieOnBlur: false, containment: "self"}');
      $video.YTPlayer();
    } else {
      $video.attr('data-property', '{videoURL: _bg_video_youtube_url, autoPlay: true, loop: _bg_video_youtube_loop, startAt: _bg_video_youtube_start, stopAt: _bg_video_youtube_end, mute: false, quality: _bg_video_youtube_quality, realfullscreen: true, optimizeDisplay: true, addRaster: false, showYTLogo: false, showControls: false, stopMovieOnBlur: false, containment: "self"}');
      $video.YTPlayer();

      $body.addClass('is-audio-on');

      $audio.on('click', function() {
        if ($body.hasClass('is-audio-on')) {
          $video.YTPMute()
          $body.removeClass('is-audio-on').addClass('is-audio-off');
        } else if ($body.hasClass('is-audio-off')) {
          $video.YTPUnmute()
          $body.removeClass('is-audio-off').addClass('is-audio-on');
        }
      });
    }
  },

//
// background audio
// --------------------------------------------------

  fn_siteBgAudio: function() {
    if (_bg_style_mobile == 1 || _bg_style_mobile == 3 || _bg_style_mobile == 5 || _bg_style_mobile == 7 || _bg_style_desktop == 1 || _bg_style_desktop == 3 || _bg_style_desktop == 5 || _bg_style_desktop == 7 || _bg_style_desktop == 10 || _bg_style_desktop == 13) {
      $body.append('<audio id="audioPlayer" loop>' +
                   '<source src="assets/audio/audio.mp3" type="audio/mpeg">' +
                   '</audio>');
    }

    if (isMobile) {
      if (_bg_style_mobile == 1 || _bg_style_mobile == 3 || _bg_style_mobile == 5 || _bg_style_mobile == 7) {
        $body.addClass('is-audio-off');
        fn_siteBgAudioControl();
      }
    } else {
      if (_bg_style_desktop == 1 || _bg_style_desktop == 3 || _bg_style_desktop == 5 || _bg_style_desktop == 7 || _bg_style_desktop == 10 || _bg_style_desktop == 14) {
        var $audioPlayer = document.getElementById('audioPlayer');

        $body.addClass('is-audio-on');
        $audioPlayer.play();
        fn_siteBgAudioControl();
      }
    }

    function fn_siteBgAudioControl() {
      var $audio = $('.audio-toggle');
      var $audioPlayer = document.getElementById('audioPlayer');

      $audio.on('click', function() {
        var $this = $(this);

        if ($body.hasClass('is-audio-on')) {
          $audioPlayer.pause();
          $body.removeClass('is-audio-on').addClass('is-audio-off');
        } else if ($body.hasClass('is-audio-off')) {
          $audioPlayer.play();
          $body.removeClass('is-audio-off').addClass('is-audio-on');
        }
      });
    }
  },

//
// animation
// --------------------------------------------------
//

  fn_siteBgAnimation: function() {
    if (_site_bg_animation === 0) {
      $('.site-bg-canvas').remove();
    } else if (_site_bg_animation == 1) {
      fn_siteBgConstellation();
    } else if (_site_bg_animation == 2) {
      fn_siteBgParallaxStar();
    } else if (_site_bg_animation == 3) {
      fn_particles();
    }

    function fn_siteBgConstellation() {
      var $canvas = $('.site-bg-canvas');

      $body.addClass('is-site-bg-constellation');

      function callCanvas (canvas) {
        var screenpointSplitt = 12000;
        var movingSpeed = 0.2;
        var viewportWidth = $(window).width();
        var viewportHeight = $(window).height();
        var nbCalculated = Math.round(viewportHeight*viewportWidth/screenpointSplitt);

        var $this = $(this),
        ctx = canvas.getContext('2d');
        $this.config = {
          star: {
            color: _constellation_color,
            width: _constellation_width
          },
          line: {
            color: _constellation_color,
            width: 0.4
          },
          position: {
            x: canvas.width * 0.5,
            y: canvas.height * 0.5
          },
          velocity: movingSpeed,
          length: nbCalculated,
          distance: 130,
          radius: 120,
          stars: []
        };

        function Star () {
          this.x = Math.random() * canvas.width;
          this.y = Math.random() * canvas.height;

          this.vx = ($this.config.velocity - (Math.random() * 0.3));
          this.vy = ($this.config.velocity - (Math.random() * 0.3));

          this.radius = Math.random() * $this.config.star.width;
        }

        Star.prototype = {
          create: function(){
            ctx.beginPath();
            ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2, false);
            ctx.fill();
          },

          animate: function(){
            var i;
            for(i = 0; i < $this.config.length; i++){

              var star = $this.config.stars[i];

              if(star.y < 0 || star.y > canvas.height){
                star.vx = star.vx;
                star.vy = - star.vy;
              }
              else if(star.x < 0 || star.x > canvas.width){
                star.vx = - star.vx;
                star.vy = star.vy;
              }
              star.x += star.vx;
              star.y += star.vy;
            }
          },

          line: function(){
            var length = $this.config.length,
              iStar,
              jStar,
              i,
              j;

            for(i = 0; i < length; i++){
              for(j = 0; j < length; j++){
                iStar = $this.config.stars[i];
                jStar = $this.config.stars[j];

                if(
                  (iStar.x - jStar.x) < $this.config.distance &&
                  (iStar.y - jStar.y) < $this.config.distance &&
                  (iStar.x - jStar.x) > - $this.config.distance &&
                  (iStar.y - jStar.y) > - $this.config.distance
                ) {
                  if(
                    (iStar.x - $this.config.position.x) < $this.config.radius &&
                    (iStar.y - $this.config.position.y) < $this.config.radius &&
                    (iStar.x - $this.config.position.x) > - $this.config.radius &&
                    (iStar.y - $this.config.position.y) > - $this.config.radius
                  ) {
                    ctx.beginPath();
                    ctx.moveTo(iStar.x, iStar.y);
                    ctx.lineTo(jStar.x, jStar.y);
                    ctx.stroke();
                    ctx.closePath();
                  }

                }
              }
            }
          }

        };
        $this.createStars = function () {
          var length = $this.config.length,
            star,
            i;

          ctx.clearRect(0, 0, canvas.width, canvas.height);
          for(i = 0; i < length; i++){
            $this.config.stars.push(new Star());
            star = $this.config.stars[i];
            star.create();
          }

          star.line();
          star.animate();
        };

        $this.setCanvas = function () {
          canvas.width = window.innerWidth;
          canvas.height = window.innerHeight;
        };

        $this.setContext = function () {
          ctx.fillStyle = $this.config.star.color;
          ctx.strokeStyle = $this.config.line.color;
          ctx.lineWidth = $this.config.line.width;
          ctx.fill();
        };

        $this.loop = function (callback) {
          callback();
          reqAnimFrame(function () {
            $this.loop(function () {
              callback();
            });
          });
        };

        $this.bind = function () {
          $(window).on('mousemove', function(e){
            $this.config.position.x = e.pageX;
            $this.config.position.y = e.pageY;
          });
        };

        $this.init = function () {
          $this.setCanvas();
          $this.setContext();

          $this.loop(function () {
            $this.createStars();
          });

          $this.bind();
        };

        return $this;
      }

      var reqAnimFrame = window.requestAnimationFrame || window.mozRequestAnimationFrame || window.webkitRequestAnimationFrame || window.msRequestAnimationFrame || function (callback) {
        window.setTimeout(callback, 1000 / 60);
      };

      $(window).on('load', function() {
        setTimeout(function () {
          callCanvas($('canvas')[0]).init();
          $canvas.velocity('transition.fadeIn', {
            duration: 3000
          });
        }, 1000);
      });

      var waitForFinalEvent = (function () {
        var timers = {};
        return function (callback, ms, uniqueId) {
        if (!uniqueId) {
          uniqueId = '';
        }
        if (timers[uniqueId]) {
          clearTimeout (timers[uniqueId]);
        }
        timers[uniqueId] = setTimeout(callback, ms);
        };
      })();

      $(window).resize(function () {
        waitForFinalEvent(function() {
          //callCanvas($('canvas')[0]).init();
          callCanvas($('canvas')[0]).init();
        }, 800, '');
      });
    }

    function fn_siteBgParallaxStar() {
      var $siteBgAnimation = $('.site-bg-animation');

      $body.addClass('is-site-bg-parallax-star');
      $('.site-bg-canvas').remove();

      $siteBgAnimation.append(
        '<div class="parallax-star"></div>' +
        '<div class="parallax-star"></div>' +
        '<div class="parallax-star"></div>'
      );
    }

    function fn_particles() {
      $body.addClass('is-site-bg-particles');
      $('.site-bg-effect, .site-bg-canvas').remove();

      particlesJS("particles-js", {
        "particles": {
          "number": {
            "value": 25,
            "density": {
              "enable": true,
              "value_area": 500
            }
          },
          "color": {
            "value": "#ffffff"
          },
          "opacity": {
            "value": _particles_opacity,
            "random": false,
            "anim": {
              "enable": false,
              "speed": 1,
              "opacity_min": 0.1,
              "sync": false
            }
          },
          "size": {
            "value": 4,
            "random": true,
            "anim": {
              "enable": false,
              "speed": 40,
              "size_min": 0.1,
              "sync": false
            }
          },
          "line_linked": {
            "enable": true,
            "distance": 150,
            "color": "#ffffff",
            "opacity": _particles_link_opacity,
            "width": 1
          },
          "move": {
            "enable": true,
            "speed": 6,
            "direction": "none",
            "random": false,
            "straight": false,
            "out_mode": "out",
            "bounce": false,
            "attract": {
              "enable": false,
              "rotateX": 600,
              "rotateY": 1200
            }
          }
        },
        "interactivity": {
          "detect_on": "canvas",
          "events": {
            "onhover": {
              "enable": false,
              "mode": "repulse"
            },
            "onclick": {
              "enable": false,
              "mode": "push"
            },
            "resize": true
          },
          "modes": {
            "grab": {
              "distance": 400,
              "line_linked": {
                "opacity": 1
              }
            },
            "bubble": {
              "distance": 400,
              "size": 40,
              "duration": 2,
              "opacity": 8,
              "speed": 3
            },
            "repulse": {
              "distance": 200,
              "duration": 0.4
            },
            "push": {
              "particles_nb": 4
            },
            "remove": {
              "particles_nb": 2
            }
          }
        },
        "retina_detect": true
      });
    }
  },

//
// parallax effect
// --------------------------------------------------
//

  fn_parallaxEffect: function() {
    if (_side_bg_effect_parallax && !isMobile && !isIE9) {
      $('.site-bg').parallax();
    }
  },

//
// scroll reveal
// --------------------------------------------------
//

  fn_scrollReveal: function() {
    if (!isMobile && !isIE9) {
      var config = {
        origin: 'bottom',
        distance: '20px',
        duration: 800,
        delay: 0,
        rotate: {x :0, y :0, z :0},
        opacity: 0,
        scale: 0,
        easing: 'ease-in-out',
        container: null,
        mobile: false,
        reset: true,
        useDelay: 'always',
        viewFactor: .2,
        viewOffset: {top : 0, right : 0, bottom : 0, left : 0},
        afterReveal: function( domEl ){},
        afterReset: function( domEl ){}
      }

      window.sr = new ScrollReveal(config);
      if ($('[data-sr=top]').length) {
        sr.reveal('[data-sr=top]', {origin: 'top'});
      }
      if ($('[data-sr=right]').length) {
        sr.reveal('[data-sr=right]', {origin: 'right'});
      }
      if ($('[data-sr=bottom]').length) {
        sr.reveal('[data-sr=bottom]', {origin: 'bottom'});
      }
      if ($('[data-sr=left]').length) {
        sr.reveal('[data-sr=left]', {origin: 'left'});
      }
    };
  },

//
// end of BONEFISHCODE
// --------------------------------------------------

  };

//
// init
// --------------------------------------------------

	$(function() {
		BONEFISHCODE.ready();

    $(window).on('scroll', function() {
      BONEFISHCODE.scroll();
    });

    $(window).on('resize', function() {
      BONEFISHCODE.resize();
    });

    $(window).on('load', function() {
      BONEFISHCODE.load();
    });
	});

//
// function end
// --------------------------------------------------

})(jQuery);