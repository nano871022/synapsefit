<!DOCTYPE html>

<html class="dark" lang="en"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>Workout Plan Detail View</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;700;800&amp;family=Inter:wght@400;600&amp;family=JetBrains+Mono:wght@500&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<script id="tailwind-config">
        tailwind.config = {
          darkMode: "class",
          theme: {
            extend: {
              "colors": {
                      "tertiary-container": "#dddde4",
                      "on-primary-container": "#006c71",
                      "secondary-fixed-dim": "#c6c6c9",
                      "on-error-container": "#ffdad6",
                      "on-surface-variant": "#b9caca",
                      "surface-container-low": "#181c1e",
                      "inverse-on-surface": "#2d3133",
                      "secondary-container": "#454749",
                      "surface-container-highest": "#323538",
                      "on-tertiary-fixed": "#191c20",
                      "on-secondary-fixed-variant": "#454749",
                      "on-error": "#690005",
                      "surface-container": "#1c2023",
                      "on-tertiary-container": "#5f6167",
                      "surface-tint": "#00dce5",
                      "primary-fixed": "#63f7ff",
                      "surface-container-high": "#272a2d",
                      "tertiary-fixed-dim": "#c5c6cc",
                      "inverse-primary": "#00696e",
                      "secondary-fixed": "#e2e2e5",
                      "primary": "#e9feff",
                      "on-primary-fixed": "#002021",
                      "tertiary-fixed": "#e2e2e8",
                      "on-primary-fixed-variant": "#004f53",
                      "error-container": "#93000a",
                      "on-background": "#e0e3e6",
                      "outline-variant": "#3a494a",
                      "surface": "#101416",
                      "on-primary": "#003739",
                      "inverse-surface": "#e0e3e6",
                      "primary-container": "#00f5ff",
                      "error": "#ffb4ab",
                      "on-secondary-container": "#b4b5b7",
                      "surface-bright": "#363a3c",
                      "on-surface": "#e0e3e6",
                      "surface-variant": "#323538",
                      "surface-container-lowest": "#0b0f11",
                      "on-tertiary-fixed-variant": "#45474c",
                      "on-tertiary": "#2e3035",
                      "outline": "#849495",
                      "background": "#101416",
                      "tertiary": "#faf9ff",
                      "on-secondary": "#2f3133",
                      "secondary": "#c6c6c9",
                      "primary-fixed-dim": "#00dce5",
                      "on-secondary-fixed": "#1a1c1e",
                      "surface-dim": "#101416"
              },
              "borderRadius": {
                      "DEFAULT": "0.25rem",
                      "lg": "0.5rem",
                      "xl": "0.75rem",
                      "full": "9999px"
              },
              "spacing": {
                      "xl": "32px",
                      "sm": "8px",
                      "xs": "4px",
                      "gutter": "16px",
                      "md": "16px",
                      "unit": "4px",
                      "margin-edge": "16px",
                      "lg": "24px"
              },
              "fontFamily": {
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
                      ],
                      "display-lg": [
                              "Hanken Grotesk"
                      ],
                      "headline-lg-mobile": [
                              "Hanken Grotesk"
                      ]
              },
              "fontSize": {
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
                      ],
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
                      ]
              }
            }
          }
      </script>
<style>
          /* Hide scrollbar for clean look */
          ::-webkit-scrollbar {
              display: none;
          }
          * {
              scrollbar-width: none;
          }
      </style>
<style>
    body {
      min-height: max(884px, 100dvh);
    }
  </style>
  </head>
<body class="bg-background text-on-background min-h-screen font-body-md pb-24 selection:bg-primary-container selection:text-on-primary-container">
<!-- Top Navigation Anchor (Modified for sub-page intent) -->
<header class="bg-surface border-b border-white/10 w-full top-0 sticky z-40 flex items-center px-margin-edge h-16 w-full">
<button class="mr-4 text-on-surface hover:text-primary transition-colors flex items-center justify-center h-10 w-10 rounded-full active:scale-95">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">arrow_back</span>
</button>
<div>
<h1 class="font-headline-lg-mobile text-headline-lg-mobile font-bold text-primary dark:text-primary-fixed-dim tracking-tight">Hipertrofia Total</h1>
<p class="font-label-sm text-label-sm text-on-surface-variant">Día 1: Empuje (Pecho/Hombro/Tríceps)</p>
</div>
</header>
<main class="max-w-2xl mx-auto px-margin-edge pt-md pb-xl">
<!-- Routine Summary Module -->
<section class="bg-surface-container rounded-xl p-md mb-gutter border border-white/10 flex justify-between items-center relative overflow-hidden">
<!-- Ambient Glow -->
<div class="absolute -right-8 -top-8 w-24 h-24 bg-primary-container rounded-full blur-[40px] opacity-20 pointer-events-none"></div>
<div class="flex flex-col items-center">
<span class="font-label-sm text-label-sm text-on-surface-variant uppercase mb-xs">Ejercicios</span>
<div class="flex items-baseline gap-1">
<span class="font-title-md text-title-md text-primary">8</span>
</div>
</div>
<div class="w-px h-8 bg-white/10"></div>
<div class="flex flex-col items-center">
<span class="font-label-sm text-label-sm text-on-surface-variant uppercase mb-xs">Duración</span>
<div class="flex items-baseline gap-1">
<span class="font-title-md text-title-md text-primary">60</span>
<span class="font-label-sm text-label-sm text-on-surface-variant">min</span>
</div>
</div>
<div class="w-px h-8 bg-white/10"></div>
<div class="flex flex-col items-center">
<span class="font-label-sm text-label-sm text-on-surface-variant uppercase mb-xs">Descanso Med.</span>
<div class="flex items-baseline gap-1">
<span class="font-title-md text-title-md text-primary">90</span>
<span class="font-label-sm text-label-sm text-on-surface-variant">s</span>
</div>
</div>
</section>
<!-- Exercise List -->
<div class="space-y-gutter">
<!-- Exercise Item 1 -->
<article class="bg-surface-container rounded-xl p-md border border-white/10 relative overflow-hidden group">
<div class="absolute inset-0 bg-primary-container/5 opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"></div>
<div class="flex gap-md mb-md">
<!-- Thumbnail -->
<div class="w-20 h-20 rounded-lg overflow-hidden bg-surface-container-high shrink-0 border border-white/5 relative">
<img class="w-full h-full object-cover" data-alt="A highly detailed, cinematic photograph of an athletic person performing a barbell bench press in a modern, moody high-tech gym. The lighting is low-key with sharp neon cyan accents outlining the equipment. Deep blacks and pristine whites dominate the aesthetic, with a cool blue undertone conveying intense performance and modern fitness technology." src="https://lh3.googleusercontent.com/aida-public/AB6AXuARjT1K_u4R1CUfh3u2ezZtU3uvrKwt0RYtR4zhBFySXBvn3D4tf8P1qsSahzmgOeg042YIHXay3P1Cx0vg2lHIavhy50jbhKUZyUTEs_lxOH_azlGnJOXaAs1ZhxqoQCDR94IKQIzD9bigrZ2eAX-_vIkXRRaIAgAv13RpJ-Xmey6TCNzj2HVvfZwko6UDKfsHHgugVwtZk-RzN6iCW8nskwPNrmzMYGse7knY7DehvH90j-IEm4ee"/>
</div>
<div class="flex-1">
<div class="flex justify-between items-start mb-1">
<h3 class="font-title-md text-title-md text-on-surface">Bench Press</h3>
<button class="text-primary-container hover:text-primary transition-colors bg-primary-container/10 px-2 py-1 rounded flex items-center gap-1 active:scale-95">
<span class="material-symbols-outlined text-[16px]">play_circle</span>
<span class="font-label-sm text-label-sm uppercase">Guía</span>
</button>
</div>
<p class="font-label-sm text-label-sm text-on-surface-variant mb-2">Barra • Compuesto</p>
<div class="flex flex-wrap gap-sm">
<div class="bg-surface-container-high px-2 py-1 rounded border border-white/5 flex items-center gap-1">
<span class="material-symbols-outlined text-[14px] text-on-surface-variant">repeat</span>
<span class="font-label-sm text-label-sm text-on-surface">4 x 8-12</span>
</div>
<div class="bg-surface-container-high px-2 py-1 rounded border border-white/5 flex items-center gap-1">
<span class="material-symbols-outlined text-[14px] text-on-surface-variant">fitness_center</span>
<span class="font-label-sm text-label-sm text-on-surface">60 kg</span>
</div>
<div class="bg-surface-container-high px-2 py-1 rounded border border-white/5 flex items-center gap-1">
<span class="material-symbols-outlined text-[14px] text-on-surface-variant">timer</span>
<span class="font-label-sm text-label-sm text-on-surface">90s</span>
</div>
</div>
</div>
</div>
</article>
<!-- Exercise Item 2 -->
<article class="bg-surface-container rounded-xl p-md border border-white/10 relative overflow-hidden group">
<div class="absolute inset-0 bg-primary-container/5 opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"></div>
<div class="flex gap-md mb-md">
<!-- Thumbnail -->
<div class="w-20 h-20 rounded-lg overflow-hidden bg-surface-container-high shrink-0 border border-white/5 relative">
<img class="w-full h-full object-cover" data-alt="A highly detailed, cinematic photograph of an athletic person performing a seated dumbbell overhead press in a modern, moody high-tech gym. The lighting is low-key with sharp neon cyan accents outlining the equipment. Deep blacks and pristine whites dominate the aesthetic, with a cool blue undertone conveying intense performance and modern fitness technology." src="https://lh3.googleusercontent.com/aida-public/AB6AXuCsV_KQy-AdbBp698ynn6E_LYZjzSqwODxDgTwMWK6lX40-S1-l8HGXG1EAZP4E8-12JaWKMDTue-VTHC5Hr3yZc5VL2msgM97vDg4OrpNXOKjugzU18ZOI4FjJpqH10D9Z7iOvdSCAWIJwRMCmisR68QLevMJFvvdfCwLc8_1OyJKidVbB9qQlpfkMvrYGLa-JvMVlF_Oa2iDJ0eCBnz5XrVtdqMc1GInTz2mrEny_atvKYuikWjkb"/>
</div>
<div class="flex-1">
<div class="flex justify-between items-start mb-1">
<h3 class="font-title-md text-title-md text-on-surface">Overhead Press</h3>
<button class="text-primary-container hover:text-primary transition-colors bg-primary-container/10 px-2 py-1 rounded flex items-center gap-1 active:scale-95">
<span class="material-symbols-outlined text-[16px]">play_circle</span>
<span class="font-label-sm text-label-sm uppercase">Guía</span>
</button>
</div>
<p class="font-label-sm text-label-sm text-on-surface-variant mb-2">Mancuernas • Compuesto</p>
<div class="flex flex-wrap gap-sm">
<div class="bg-surface-container-high px-2 py-1 rounded border border-white/5 flex items-center gap-1">
<span class="material-symbols-outlined text-[14px] text-on-surface-variant">repeat</span>
<span class="font-label-sm text-label-sm text-on-surface">3 x 10-12</span>
</div>
<div class="bg-surface-container-high px-2 py-1 rounded border border-white/5 flex items-center gap-1">
<span class="material-symbols-outlined text-[14px] text-on-surface-variant">fitness_center</span>
<span class="font-label-sm text-label-sm text-on-surface">20 kg (c/u)</span>
</div>
<div class="bg-surface-container-high px-2 py-1 rounded border border-white/5 flex items-center gap-1">
<span class="material-symbols-outlined text-[14px] text-on-surface-variant">timer</span>
<span class="font-label-sm text-label-sm text-on-surface">90s</span>
</div>
</div>
</div>
</div>
</article>
<!-- Exercise Item 3 -->
<article class="bg-surface-container rounded-xl p-md border border-white/10 relative overflow-hidden group">
<div class="absolute inset-0 bg-primary-container/5 opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"></div>
<div class="flex gap-md mb-md">
<!-- Thumbnail -->
<div class="w-20 h-20 rounded-lg overflow-hidden bg-surface-container-high shrink-0 border border-white/5 relative">
<img class="w-full h-full object-cover" data-alt="A highly detailed, cinematic photograph of an athletic person performing an incline dumbbell press in a modern, moody high-tech gym. The lighting is low-key with sharp neon cyan accents outlining the equipment. Deep blacks and pristine whites dominate the aesthetic, with a cool blue undertone conveying intense performance and modern fitness technology." src="https://lh3.googleusercontent.com/aida-public/AB6AXuBBGgP5JpdQXwJ0QsVCsVGyeQPuGiImv_kXdoHHMAcTXbRRTWLnJSpAhn3QcqexdNK3y1eqVnAc_IzxgrJvDNX4bgRIZ8Km8UVM6x_2WW9Z27YpTfw9Sv1DNxzO1HKtfoRCHn0RIUMhdgmoe5iV1TXyoBB0LT1tcVLvnOQOy6MJCx5q-z7wbBFNPsvdDiITHwXiDnrRlIy7NGi2_PnMW6zq2CsYyABGnue8D4OXhLDbevbBH_03Mp63"/>
</div>
<div class="flex-1">
<div class="flex justify-between items-start mb-1">
<h3 class="font-title-md text-title-md text-on-surface">Incline DB Press</h3>
<button class="text-primary-container hover:text-primary transition-colors bg-primary-container/10 px-2 py-1 rounded flex items-center gap-1 active:scale-95">
<span class="material-symbols-outlined text-[16px]">play_circle</span>
<span class="font-label-sm text-label-sm uppercase">Guía</span>
</button>
</div>
<p class="font-label-sm text-label-sm text-on-surface-variant mb-2">Mancuernas • Compuesto</p>
<div class="flex flex-wrap gap-sm">
<div class="bg-surface-container-high px-2 py-1 rounded border border-white/5 flex items-center gap-1">
<span class="material-symbols-outlined text-[14px] text-on-surface-variant">repeat</span>
<span class="font-label-sm text-label-sm text-on-surface">3 x 10-15</span>
</div>
<div class="bg-surface-container-high px-2 py-1 rounded border border-white/5 flex items-center gap-1">
<span class="material-symbols-outlined text-[14px] text-on-surface-variant">fitness_center</span>
<span class="font-label-sm text-label-sm text-on-surface">24 kg (c/u)</span>
</div>
<div class="bg-surface-container-high px-2 py-1 rounded border border-white/5 flex items-center gap-1">
<span class="material-symbols-outlined text-[14px] text-on-surface-variant">timer</span>
<span class="font-label-sm text-label-sm text-on-surface">90s</span>
</div>
</div>
</div>
</div>
</article>
<!-- Exercise Item 4 -->
<article class="bg-surface-container rounded-xl p-md border border-white/10 relative overflow-hidden group">
<div class="absolute inset-0 bg-primary-container/5 opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"></div>
<div class="flex gap-md mb-md">
<!-- Thumbnail -->
<div class="w-20 h-20 rounded-lg overflow-hidden bg-surface-container-high shrink-0 border border-white/5 relative">
<img class="w-full h-full object-cover" data-alt="A highly detailed, cinematic photograph of an athletic person performing tricep pushdowns with a rope attachment on a cable machine in a modern, moody high-tech gym. The lighting is low-key with sharp neon cyan accents outlining the equipment. Deep blacks and pristine whites dominate the aesthetic, with a cool blue undertone conveying intense performance and modern fitness technology." src="https://lh3.googleusercontent.com/aida-public/AB6AXuCCeCwgOES8fUsfS0QwsoH8UN02tA1_AHbcJPYUrL18e4eyNnmBxfuIwjnV4Xn6DmsbFbuJLy5NrV8THDy3x26_I-cmBy3QQX9WySe8Ef4GYq6b9qeWPTjSGbs7rhDbwC-fTBBGkxsDW2ub9dMIpYlcyus6pG_AknLV4TWSHMsY-kzfKyTlqsNKWe-wpcdRPqJM1g-b5SVh1JVmqfPlQHiceF55-KJOzJWah0QxZBNInimnaudwh_MN"/>
</div>
<div class="flex-1">
<div class="flex justify-between items-start mb-1">
<h3 class="font-title-md text-title-md text-on-surface">Tricep Pushdown</h3>
<button class="text-primary-container hover:text-primary transition-colors bg-primary-container/10 px-2 py-1 rounded flex items-center gap-1 active:scale-95">
<span class="material-symbols-outlined text-[16px]">play_circle</span>
<span class="font-label-sm text-label-sm uppercase">Guía</span>
</button>
</div>
<p class="font-label-sm text-label-sm text-on-surface-variant mb-2">Polea • Aislamiento</p>
<div class="flex flex-wrap gap-sm">
<div class="bg-surface-container-high px-2 py-1 rounded border border-white/5 flex items-center gap-1">
<span class="material-symbols-outlined text-[14px] text-on-surface-variant">repeat</span>
<span class="font-label-sm text-label-sm text-on-surface">4 x 12-15</span>
</div>
<div class="bg-surface-container-high px-2 py-1 rounded border border-white/5 flex items-center gap-1">
<span class="material-symbols-outlined text-[14px] text-on-surface-variant">fitness_center</span>
<span class="font-label-sm text-label-sm text-on-surface">15 kg</span>
</div>
<div class="bg-surface-container-high px-2 py-1 rounded border border-white/5 flex items-center gap-1">
<span class="material-symbols-outlined text-[14px] text-on-surface-variant">timer</span>
<span class="font-label-sm text-label-sm text-on-surface">60s</span>
</div>
</div>
</div>
</div>
</article>
</div>
<!-- Primary Action FAB (Start Workout) -->
<div class="fixed bottom-24 right-4 md:right-8 z-40">
<button class="bg-primary-container text-on-primary-container w-14 h-14 rounded-full flex items-center justify-center shadow-[0_0_20px_rgba(0,245,255,0.3)] hover:scale-105 active:scale-95 transition-all">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">play_arrow</span>
</button>
</div>
</main>
<!-- Bottom Navigation Bar (Modified from JSON) -->
<nav class="bg-surface border-t border-white/10 fixed bottom-0 w-full z-50 fixed bottom-0 w-full flex justify-around items-center py-2 px-margin-edge bg-surface/95 backdrop-blur-md md:hidden">
<button class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary transition-colors active:scale-90 transition-transform duration-200 w-16 group">
<span class="material-symbols-outlined mb-1 group-hover:opacity-80 transition-opacity">home</span>
<span class="font-label-sm text-label-sm">Home</span>
</button>
<button class="flex flex-col items-center justify-center bg-on-primary-fixed-variant/20 text-primary-fixed-dim rounded-xl px-4 py-1 hover:text-primary transition-colors active:scale-90 transition-transform duration-200 group">
<span class="material-symbols-outlined mb-1" style="font-variation-settings: 'FILL' 1;">fitness_center</span>
<span class="font-label-sm text-label-sm">Workouts</span>
</button>
<button class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary transition-colors active:scale-90 transition-transform duration-200 w-16 group">
<span class="material-symbols-outlined mb-1 group-hover:opacity-80 transition-opacity">insights</span>
<span class="font-label-sm text-label-sm">Progress</span>
</button>
<button class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary transition-colors active:scale-90 transition-transform duration-200 w-16 group">
<span class="material-symbols-outlined mb-1 group-hover:opacity-80 transition-opacity">person</span>
<span class="font-label-sm text-label-sm">Profile</span>
</button>
</nav>
</body></html>
