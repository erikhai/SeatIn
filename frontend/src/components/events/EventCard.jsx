import { Link } from "react-router-dom";

export default function EventCard({ event }) {
  return (
    <article className="group overflow-hidden rounded-xl border bg-white hover:shadow-md transition-shadow">
      <div className="aspect-[16/9] overflow-hidden">
        <img
          src={event.image}
          alt={event.title}
          className="h-full w-full object-cover group-hover:scale-[1.02] transition-transform"
          loading="lazy"
        />
      </div>

      <div className="p-4">
        <h3 className="text-base font-semibold line-clamp-1">{event.title}</h3>
        <p className="mt-1 text-sm text-gray-600 line-clamp-2">
          {event.venue} • {event.date}
        </p>

        <div className="mt-3 flex items-center justify-between">
          <span className="text-sm font-semibold">${event.price}</span>
          <div className="flex gap-2">
            <Link
              to={`/auth/view-event-description?id=${encodeURIComponent(event.id)}`}
              className="text-sm rounded-lg border px-3 py-1.5 hover:bg-gray-50"
            >
              Details
            </Link>
            <Link
              to="/auth/events"
              className="text-sm rounded-lg bg-blue-600 px-3 py-1.5 text-white hover:bg-blue-700"
            >
              Buy
            </Link>
          </div>
        </div>
      </div>
    </article>
  );
}
