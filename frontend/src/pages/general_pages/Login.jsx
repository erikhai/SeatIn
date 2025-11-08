import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';


/**
 * Login page matching app styling
 * - Tailwind-based UI
 * - Email + password
 * - Remember me checkbox
 * - Forgot password link
 * - Sign up link
 * - Calls `${REACT_APP_BASE_URL}/login` by default; adjust if your API differs
 */

const Login = () => {
  const navigate = useNavigate();
  const BASE_URL = process.env.REACT_APP_BASE_URL;

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [remember, setRemember] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');


  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) navigate('/auth/dashboard');
  }, [navigate]);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);

    try {
      



      const response = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });
      if(!response.ok) {
   
        window.alert("Invalid credentials")
      }

    

      const loginData = await response.json();
      localStorage.setItem('accessToken', loginData.token);
      setError('');
      navigate("/auth/dashboard");


    } catch (err) {
      setError(err.message || 'Something went wrong, please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>


      {/* Page wrapper */}
      <div className="min-h-[calc(100vh-64px)] flex items-center justify-center bg-gradient-to-b from-blue-50 to-white px-4 py-12">
        <div className="w-full max-w-md">
          {/* Card */}
          <div className="bg-white/90 backdrop-blur rounded-2xl shadow-xl border border-blue-100">
            {/* Header */}
            <div className="px-8 pt-8">
              <h1 className="text-2xl font-bold text-blue-700">Welcome back</h1>
              <p className="text-sm text-gray-500 mt-1">Sign in to continue to your dashboard.</p>
            </div>

            {/* Error */}
            {error && (
              <div className="mx-8 mt-4 rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">
                {error}
              </div>
            )}

            {/* Form */}
            <form onSubmit={onSubmit} className="px-8 pb-8 pt-6 space-y-4">
              <div>
                <label htmlFor="email" className="block text-sm font-medium text-gray-700">Email</label>
                <input
                  id="email"
                  type="email"
                  autoComplete="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="mt-1 w-full rounded-xl border border-gray-300 bg-white px-4 py-2.5 text-gray-900 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
                  placeholder="you@example.com"
                />
              </div>

              <div>
               
                <input
                  id="password"
                  type="password"
                  autoComplete="current-password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="mt-1 w-full rounded-xl border border-gray-300 bg-white px-4 py-2.5 text-gray-900 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
                  placeholder="••••••••"
                />
              </div>


              <button
                type="submit"
                disabled={submitting}
                className="w-full inline-flex items-center justify-center rounded-xl bg-blue-600 px-4 py-2.5 font-semibold text-white shadow hover:bg-blue-700 focus:outline-none focus:ring-4 focus:ring-blue-200 disabled:cursor-not-allowed disabled:opacity-70"
              >
                {submitting ? 'Signing in…' : 'Sign in'}
              </button>

              {/* Divider */}
              <div className="relative my-2">
                <div className="absolute inset-0 flex items-center" aria-hidden="true">
                  <div className="w-full border-t border-gray-200" />
                </div>
                <div className="relative flex justify-center">
                  <span className="bg-white px-2 text-xs text-gray-500">or</span>
                </div>
              </div>

              {/* Sign up */}
              <p className="text-center text-sm text-gray-600">
                Don&apos;t have an account?{' '}
                <Link to="/signup" className="font-semibold text-blue-600 hover:underline">
                  Create one
                </Link>
              </p>
            </form>
          </div>

          
        </div>
      </div>
    </>
  );
};

export default Login;