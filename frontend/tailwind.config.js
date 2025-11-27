/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#dc2626',
          dark: '#b91c1c',
          light: '#ef4444'
        },
        dark: {
          DEFAULT: '#050505',
          secondary: '#1a1a1a',
          card: '#0f0f0f'
        }
      }
    },
  },
  plugins: [],
}
