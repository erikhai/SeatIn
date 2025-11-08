
// frontend/src/pages/general_pages/Landing.jsx
import LandingCarousel from "../../components/global/LandingCarousel";

export default function Landing() {

  return (
    <main className="mx-auto max-w-7xl px-6 py-10 space-y-16">
      {/* Hero */}
      <section className="grid grid-cols-1 md:grid-cols-2 gap-10 items-center">
        <div className="space-y-6">
          <h1 className="text-4xl md:text-5xl font-bold text-gray-900 leading-tight">
            SeatIn — Design your venue, let guests book exact seats
          </h1>
          <p className="text-lg text-gray-600">
            Organisers can create fully custom seating maps—place rows, aisles,
            VIP zones, and the stage/speaker area exactly where you want. Guests
            then pick their exact seats during checkout for a smooth, transparent
            experience.
          </p>
          <p className="text-gray-600">
            From intimate meetups to large conferences, SeatIn helps you plan
            every detail of the room so attendees know precisely where they’ll
            sit.
          </p>
        </div>

        <div className="w-full">
          <LandingCarousel />
        </div>
      </section>

      {/* Key Selling Points */}
      <section className="space-y-6">
        <h2 className="text-2xl font-semibold text-gray-900">Why SeatIn</h2>
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <div className="rounded-2xl border bg-white p-6 shadow-sm">
            <h3 className="font-semibold text-gray-900">Customisable Venues</h3>
            <p className="mt-2 text-sm text-gray-600">
              Drag-and-drop seat placements, define sections, and mark the stage or
              speaker area to match the real layout.
            </p>
          </div>
          <div className="rounded-2xl border bg-white p-6 shadow-sm">
            <h3 className="font-semibold text-gray-900">Exact Seat Booking</h3>
            <p className="mt-2 text-sm text-gray-600">
              Guests choose their exact seats at checkout—no guesswork, fewer
              support requests.
            </p>
          </div>
          <div className="rounded-2xl border bg-white p-6 shadow-sm">
            <h3 className="font-semibold text-gray-900">Tiered Pricing</h3>
            <p className="mt-2 text-sm text-gray-600">
              Create pricing tiers (e.g., VIP, Premium, Standard) by area or row
              to maximise revenue.
            </p>
          </div>
          <div className="rounded-2xl border bg-white p-6 shadow-sm">
            <h3 className="font-semibold text-gray-900">Real-time Insights</h3>
            <p className="mt-2 text-sm text-gray-600">
              Track sell-through, revenue by tier, and capacity in real time.
            </p>
          </div>
        </div>
      </section>


      {/* How it works */}
      <section className="space-y-6">
        <h2 className="text-2xl font-semibold text-gray-900">How it works</h2>
        <ol className="grid md:grid-cols-3 gap-6 list-decimal list-inside">
          <li className="rounded-2xl border bg-white p-6 shadow-sm">
            <h3 className="font-semibold text-gray-900">Create your event</h3>
            <p className="mt-2 text-sm text-gray-600">
              Add the basics—title, description, time, and location.
            </p>
          </li>
          <li className="rounded-2xl border bg-white p-6 shadow-sm">
            <h3 className="font-semibold text-gray-900">Design the venue</h3>
            <p className="mt-2 text-sm text-gray-600">
              Place chairs, aisles, and the stage/speaker area. Assign colours and
              prices to different sections.
            </p>
          </li>
          <li className="rounded-2xl border bg-white p-6 shadow-sm">
            <h3 className="font-semibold text-gray-900">Share & let guests book</h3>
            <p className="mt-2 text-sm text-gray-600">
              Publish your event and let guests pick their exact seats with a
              live, interactive map.
            </p>
          </li>
        </ol>
      </section>


      {/* Note: no create/browse buttons on the home page as requested */}
      <section className="rounded-2xl border bg-blue-50 p-6">
        <p className="text-sm text-blue-900">
          Tip: Use the Login button in the top right when you’re ready to manage
          events or bookings.
        </p>
      </section>
    </main>
  );
}
