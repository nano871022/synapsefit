<!DOCTYPE html>

<html class="dark" lang="en"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>Backup &amp; Sync - Kinetic Pulse</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com" rel="preconnect"/>
<link crossorigin="" href="https://fonts.gstatic.com" rel="preconnect"/>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;700;800&amp;family=Inter:wght@400;600&amp;family=JetBrains+Mono:wght@500&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<script id="tailwind-config">
        tailwind.config = {
          darkMode: "class",
          theme: {
            extend: {
              "colors": {
                      "on-primary-fixed": "#002021",
                      "secondary-fixed-dim": "#c6c6c9",
                      "on-tertiary-fixed-variant": "#45474c",
                      "surface-variant": "#323538",
                      "on-tertiary": "#2e3035",
                      "on-primary-container": "#006c71",
                      "surface-bright": "#363a3c",
                      "surface-container-lowest": "#0b0f11",
                      "tertiary-container": "#dddde4",
                      "primary-fixed": "#63f7ff",
                      "surface-container": "#1c2023",
                      "inverse-on-surface": "#2d3133",
                      "primary-container": "#00f5ff",
                      "error": "#ffb4ab",
                      "on-background": "#e0e3e6",
                      "surface-tint": "#00dce5",
                      "background": "#101416",
                      "inverse-primary": "#00696e",
                      "on-secondary-container": "#b4b5b7",
                      "secondary-fixed": "#e2e2e5",
                      "surface-container-low": "#181c1e",
                      "tertiary": "#faf9ff",
                      "on-primary": "#003739",
                      "on-secondary-fixed-variant": "#454749",
                      "outline": "#849495",
                      "surface-container-highest": "#323538",
                      "inverse-surface": "#e0e3e6",
                      "on-error-container": "#ffdad6",
                      "surface-dim": "#101416",
                      "secondary-container": "#454749",
                      "surface-container-high": "#272a2d",
                      "on-primary-fixed-variant": "#004f53",
                      "primary-fixed-dim": "#00dce5",
                      "outline-variant": "#3a494a",
                      "on-tertiary-fixed": "#191c20",
                      "secondary": "#c6c6c9",
                      "tertiary-fixed-dim": "#c5c6cc",
                      "on-secondary": "#2f3133",
                      "tertiary-fixed": "#e2e2e8",
                      "on-surface": "#e0e3e6",
                      "primary": "#e9feff",
                      "surface": "#101416",
                      "error-container": "#93000a",
                      "on-tertiary-container": "#5f6167",
                      "on-secondary-fixed": "#1a1c1e",
                      "on-error": "#690005",
                      "on-surface-variant": "#b9caca"
              },
              "borderRadius": {
                      "DEFAULT": "0.25rem",
                      "lg": "0.5rem",
                      "xl": "0.75rem",
                      "full": "9999px"
              },
              "spacing": {
                      "xl": "32px",
                      "lg": "24px",
                      "sm": "8px",
                      "md": "16px",
                      "margin-edge": "16px",
                      "xs": "4px",
                      "gutter": "16px",
                      "unit": "4px"
              },
              "fontFamily": {
                      "display-lg": [
                              "Hanken Grotesk"
                      ],
                      "headline-lg-mobile": [
                              "Hanken Grotesk"
                      ],
                      "label-sm": [
                              "JetBrains Mono"
                      ],
                      "headline-lg": [
                              "Hanken Grotesk"
                      ],
                      "title-md": [
                              "Inter"
                      ],
                      "body-md": [
                              "Inter"
                      ]
              },
              "fontSize": {
                      "display-lg": [
                              "48px",
                              {
                                      "lineHeight": "56px",
                                      "letterSpacing": "-0.02em",
                                      "fontWeight": "800"
                              }
                      ],
                      "headline-lg-mobile": [
                              "28px",
                              {
                                      "lineHeight": "36px",
                                      "fontWeight": "700"
                              }
                      ],
                      "label-sm": [
                              "12px",
                              {
                                      "lineHeight": "16px",
                                      "letterSpacing": "0.05em",
                                      "fontWeight": "500"
                              }
                      ],
                      "headline-lg": [
                              "32px",
                              {
                                      "lineHeight": "40px",
                                      "fontWeight": "700"
                              }
                      ],
                      "title-md": [
                              "18px",
                              {
                                      "lineHeight": "24px",
                                      "fontWeight": "600"
                              }
                      ],
                      "body-md": [
                              "16px",
                              {
                                      "lineHeight": "24px",
                                      "fontWeight": "400"
                              }
                      ]
              }
      },
          },
        }
      </script>
<style>
        .module-glow {
            box-shadow: 0 0 20px rgba(0, 245, 255, 0.05);
        }
    </style>
<style>
    body {
      min-height: max(884px, 100dvh);
    }
  </style>
  </head>
<body class="bg-background text-on-background min-h-screen pb-[88px] selection:bg-primary-container selection:text-on-primary-container font-body-md antialiased md:pb-0">
<!-- TopAppBar Semantic Shell -->
<header class="bg-surface dark:bg-surface flex justify-between items-center px-margin-edge h-16 w-full max-w-7xl mx-auto z-40 sticky top-0 md:hidden">
<div class="flex items-center gap-sm">
<button class="w-10 h-10 rounded-full bg-surface-container-high flex items-center justify-center overflow-hidden border border-outline-variant/30">
<img class="w-full h-full object-cover" data-alt="A macro shot of a sleek, dark metallic biometric sensor interface glowing subtly with neon cyan light, evoking high-tech precision and personal health tracking. The lighting is low-key, moody, and highly detailed." src="https://lh3.googleusercontent.com/aida-public/AB6AXuDzAqxERQHyxWh7G-f0mRq-iQ0yCsHOXqpySM4wPdBRWcFQLnYg_IgxJAnOUTC33k_OE9zKfIozKn9lZ4onHU7-rDNGqLT2Z3xBELT9RUZhM4TAoU22KSJjRcGmFiAz1Mo6rHITgqhegFQqn1kd9C0LERssKyWPqhuI-5uLR4OlL0G_udeDhKo7nPeRNBD3wDdL-4yp1DKPbdvu4fSk5qrZVC0scORhqIeHR0YWZ-C55d7V9v0jG3P0"/>
</button>
<h1 class="font-headline-lg-mobile text-headline-lg-mobile font-bold text-primary-container dark:text-primary-fixed">Kinetic Pulse</h1>
</div>
<button class="w-10 h-10 flex items-center justify-center rounded-full hover:bg-surface-variant/10 transition-colors duration-200">
<span class="material-symbols-outlined text-on-surface" data-icon="notifications">notifications</span>
</button>
</header>
<!-- Web Header -->
<header class="hidden md:flex bg-surface dark:bg-surface justify-between items-center px-margin-edge h-16 w-full max-w-7xl mx-auto z-40 sticky top-0 border-b border-outline-variant/20">
<div class="flex items-center gap-md">
<h1 class="font-headline-lg text-headline-lg font-bold text-primary-container dark:text-primary-fixed">Kinetic Pulse</h1>
<nav class="flex gap-md ml-lg">
<a class="text-on-surface-variant font-title-md text-title-md hover:text-primary-fixed-dim transition-colors" href="#">Home</a>
<a class="text-on-surface-variant font-title-md text-title-md hover:text-primary-fixed-dim transition-colors" href="#">Workouts</a>
<a class="text-on-surface-variant font-title-md text-title-md hover:text-primary-fixed-dim transition-colors" href="#">Progress</a>
<a class="text-on-surface-variant font-title-md text-title-md hover:text-primary-fixed-dim transition-colors" href="#">Profile</a>
<a class="text-on-surface font-title-md text-title-md border-b-2 border-primary-container pb-1" href="#">Settings</a>
</nav>
</div>
<div class="flex items-center gap-sm">
<button class="w-10 h-10 flex items-center justify-center rounded-full hover:bg-surface-variant/10 transition-colors duration-200">
<span class="material-symbols-outlined text-on-surface" data-icon="notifications">notifications</span>
</button>
<button class="w-10 h-10 rounded-full bg-surface-container-high flex items-center justify-center overflow-hidden border border-outline-variant/30">
<img class="w-full h-full object-cover" data-alt="A macro shot of a sleek, dark metallic biometric sensor interface glowing subtly with neon cyan light, evoking high-tech precision and personal health tracking. The lighting is low-key, moody, and highly detailed." src="https://lh3.googleusercontent.com/aida-public/AB6AXuClGPVvrt02T09hTT6O8soHDLa6W5CR3JInBKE953cJbpVjcOdL_JGOM1DThLRgE5zLu_JNBZcp9D1WOwfno8skalffa42GRTsbnOmyn3PDkMAJtA2XTNP0jXQV5Clxl0U-d0Acu96vLxzcOYb3eZX_VLDnke6eo3iHgmf_gy19r4OmesVvya-LsR1aAD71B-UjkBsQVsBsIs6EAuHF1v7Y-QVvVtURWOBWNz10CrAHXUVyKvWhCNeZ"/>
</button>
</div>
</header>
<!-- Main Content Canvas -->
<main class="w-full max-w-4xl mx-auto px-margin-edge pt-lg md:pt-xl pb-xl space-y-gutter">
<!-- Header -->
<div class="mb-lg">
<div class="flex items-center gap-sm mb-xs">
<button class="w-8 h-8 rounded-full flex items-center justify-center hover:bg-surface-container transition-colors md:hidden">
<span class="material-symbols-outlined" data-icon="arrow_back">arrow_back</span>
</button>
<h2 class="font-headline-lg-mobile text-headline-lg-mobile md:font-headline-lg md:text-headline-lg text-on-surface">Backup &amp; Sync</h2>
</div>
<p class="text-on-surface-variant font-body-md text-body-md pl-10 md:pl-0 max-w-xl">
                Secure your health metrics and workout history using decentralized-grade cloud synchronization.
            </p>
</div>
<!-- Google Drive Integration Module -->
<section class="bg-surface-container-low rounded-xl p-md border border-white/5 relative overflow-hidden module-glow">
<div class="absolute inset-0 bg-gradient-to-br from-primary-container/5 to-transparent pointer-events-none"></div>
<div class="flex items-start justify-between relative z-10">
<div class="flex gap-md items-start">
<div class="w-12 h-12 rounded-lg bg-surface flex items-center justify-center border border-outline-variant/30 shrink-0">
<img alt="Google Drive" class="w-6 h-6" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAol_RQVFQF_m-m0mgI7SulLku_IQ2nc0F1pljoP_4zq2lyDE9fBTKmy6In89ui310vx2fl3ZgX_cvNSLOE2sZb4NuZpn7BtFlTV_OspwVMojoepAp0it7TX7U8fz5q3mVu42oBCwXwVQyXetcN2wtjUrW63HKp9JG1dYpIIoHaFo6exYySsLxrijztW4JIEmFEE1SQ0JNgZANYC6DPyC_TZSXxBrVEuTfP63oO_30aSUzs6dFEGowq"/>
</div>
<div>
<h3 class="font-title-md text-title-md text-on-surface mb-xs">Google Drive Connected</h3>
<p class="font-body-md text-body-md text-on-surface-variant">kinetic.athlete@gmail.com</p>
<p class="font-label-sm text-label-sm text-on-surface-variant mt-sm">Data stored securely in AppData folder. Inaccessible to other apps.</p>
</div>
</div>
<!-- Status Badge -->
<div class="bg-primary-container/10 border border-primary-container/20 rounded-full px-3 py-1 flex items-center gap-2 shrink-0">
<div class="w-2 h-2 rounded-full bg-primary-container shadow-[0_0_8px_rgba(0,245,255,0.8)]"></div>
<span class="font-label-sm text-label-sm text-primary-container uppercase tracking-widest">Synced</span>
</div>
</div>
</section>
<!-- Backup Status & Technical Data -->
<section class="bg-surface-container rounded-xl p-md border border-white/5 grid grid-cols-1 md:grid-cols-2 gap-md">
<div>
<h4 class="font-label-sm text-label-sm text-on-surface-variant uppercase tracking-widest mb-xs">Last Backup</h4>
<div class="flex items-center gap-sm">
<span class="material-symbols-outlined text-primary-container" data-icon="cloud_done">cloud_done</span>
<span class="font-title-md text-title-md text-on-surface">Today, 14:30</span>
</div>
<p class="font-body-md text-body-md text-on-surface-variant mt-xs">Size: 4.2 MB (Compressed)</p>
</div>
<div class="bg-surface-container-lowest rounded-lg p-sm border border-outline-variant/30 font-label-sm text-label-sm text-on-surface-variant flex flex-col justify-center">
<div class="flex justify-between items-center mb-xs">
<span class="text-on-surface opacity-70">Integrity Hash (SHA-256)</span>
<span class="material-symbols-outlined text-[16px] hover:text-primary-container cursor-pointer transition-colors" data-icon="content_copy">content_copy</span>
</div>
<span class="text-primary-container/80 break-all select-all font-mono">e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855</span>
</div>
</section>
<!-- Settings Toggles -->
<section class="bg-surface-container-low rounded-xl border border-white/5 divide-y divide-outline-variant/20">
<label class="flex items-center justify-between p-md cursor-pointer hover:bg-surface-variant/10 transition-colors group">
<div>
<h4 class="font-title-md text-title-md text-on-surface group-hover:text-primary-container transition-colors">Auto-Sync</h4>
<p class="font-body-md text-body-md text-on-surface-variant">Automatically backup data over Wi-Fi when charging.</p>
</div>
<div class="relative w-12 h-6 bg-surface-container-highest rounded-full border border-outline-variant/50">
<div class="absolute left-1 top-1 w-4 h-4 bg-primary-container rounded-full shadow-md transform translate-x-6 transition-transform"></div>
</div>
</label>
<label class="flex items-center justify-between p-md cursor-pointer hover:bg-surface-variant/10 transition-colors group">
<div>
<h4 class="font-title-md text-title-md text-on-surface group-hover:text-primary-container transition-colors">Include Activity Media</h4>
<p class="font-body-md text-body-md text-on-surface-variant">Backup route images and workout videos (Warning: Large sizes).</p>
</div>
<div class="relative w-12 h-6 bg-surface-container-highest rounded-full border border-outline-variant/50">
<div class="absolute left-1 top-1 w-4 h-4 bg-on-surface-variant rounded-full shadow-md transition-transform"></div>
</div>
</label>
</section>
<!-- Primary Actions -->
<section class="flex flex-col md:flex-row gap-md pt-md">
<button class="flex-1 bg-primary-container text-on-primary-container font-label-sm text-label-sm uppercase tracking-widest font-bold py-4 px-6 rounded-lg hover:bg-primary-fixed-dim transition-colors shadow-[0_0_15px_rgba(0,245,255,0.2)] hover:shadow-[0_0_25px_rgba(0,245,255,0.4)] flex items-center justify-center gap-sm">
<span class="material-symbols-outlined" data-icon="sync" style="font-variation-settings: 'FILL' 1;">sync</span>
                Sincronizar Ahora
            </button>
<button class="flex-1 border border-primary-container text-primary-container font-label-sm text-label-sm uppercase tracking-widest font-bold py-4 px-6 rounded-lg hover:bg-primary-container/10 transition-colors flex items-center justify-center gap-sm">
<span class="material-symbols-outlined" data-icon="restore" style="font-variation-settings: 'FILL' 1;">restore</span>
                Restaurar Copia de Seguridad
            </button>
</section>
</main>
<!-- BottomNavBar Semantic Shell (Mobile Only) -->
<nav class="md:hidden fixed bottom-0 w-full z-50 flex justify-around items-center px-4 py-2 bg-surface-container-low dark:bg-surface-container-low border-t border-outline-variant shadow-lg rounded-t-xl">
<a class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary-fixed-dim transition-colors duration-200" href="#">
<span class="material-symbols-outlined text-[24px]" data-icon="dashboard">dashboard</span>
<span class="font-label-sm text-label-sm mt-1">Home</span>
</a>
<a class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary-fixed-dim transition-colors duration-200" href="#">
<span class="material-symbols-outlined text-[24px]" data-icon="fitness_center">fitness_center</span>
<span class="font-label-sm text-label-sm mt-1">Workouts</span>
</a>
<a class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary-fixed-dim transition-colors duration-200" href="#">
<span class="material-symbols-outlined text-[24px]" data-icon="monitoring">monitoring</span>
<span class="font-label-sm text-label-sm mt-1">Progress</span>
</a>
<a class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary-fixed-dim transition-colors duration-200" href="#">
<span class="material-symbols-outlined text-[24px]" data-icon="person">person</span>
<span class="font-label-sm text-label-sm mt-1">Profile</span>
</a>
</nav>
</body></html>
