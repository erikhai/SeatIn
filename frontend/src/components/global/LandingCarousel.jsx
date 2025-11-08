import Slider from "react-slick";
import "slick-carousel/slick/slick.css";        
import "slick-carousel/slick/slick-theme.css";
import { Link } from "react-router-dom";


export default function LandingCarousel({ slides }) {
  const defaultSlides = [
    {
      src: "https://images.unsplash.com/photo-1540574163026-643ea20ade25?q=80&w=1200&auto=format&fit=crop",
      title: "Design unforgettable events",
      subtitle: "Seat maps, tickets, and analytics in one place.",
      to: "/auth/create-event",
    },
    {
      src: "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?q=80&w=1200&auto=format&fit=crop",
      title: "Sell out smarter",
      subtitle: "Delight guests with a beautiful booking flow.",
      to: "/auth/events",
    },
    {
      src: "https://images.unsplash.com/photo-1531058020387-3be344556be6?q=80&w=1200&auto=format&fit=crop",
      title: "Insights that matter",
      subtitle: "Track registrations, revenue, and attendance live.",
      to: "/auth/analytics",
    },
  ];

  const items = slides?.length ? slides : defaultSlides;

  const settings = {
    dots: true,
    arrows: true,
    infinite: true,
    autoplay: true,
    autoplaySpeed: 4500,
    speed: 600,
    slidesToShow: 1,
    slidesToScroll: 1,
    pauseOnHover: true,
    adaptiveHeight: false,
    responsive: [
      { breakpoint: 768, settings: { arrows: false } }, 
    ],
  };

  return (
    <div className="relative">
      <Slider {...settings} className="[&_.slick-dots_li_button:before]:text-xs md:[&_.slick-dots_li_button:before]:text-base">
        {items.map((s, idx) => {
          const Image = (
            <div className="relative">

              <div className="w-full h-[36vh] md:h-[60vh]">
                <img
                  src={s.src}
                  alt={s.title || `slide-${idx + 1}`}
                  className="h-full w-full object-cover rounded-xl"
                  loading="eager"
                />
              </div>


              {(s.title || s.subtitle) && (
                <div className="absolute inset-0 rounded-xl bg-black/30" />
              )}

              {(s.title || s.subtitle) && (
                <div className="absolute inset-0 flex items-end md:items-center p-6 md:p-10">
                  <div className="max-w-3xl">
                    {s.title && (
                      <h2 className="text-white text-xl md:text-4xl font-semibold drop-shadow-sm">
                        {s.title}
                      </h2>
                    )}
                    {s.subtitle && (
                      <p className="mt-2 text-white/90 text-sm md:text-base">
                        {s.subtitle}
                      </p>
                    )}
                  </div>
                </div>
              )}
            </div>
          );


          return (
            <div key={idx} className="px-1 md:px-2">
              {s.to ? (
                <Link to={s.to} aria-label={s.title || `slide-${idx + 1}`}>
                  {Image}
                </Link>
              ) : (
                Image
              )}
            </div>
          );
        })}
      </Slider>
    </div>
  );
}
