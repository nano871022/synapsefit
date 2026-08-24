<!DOCTYPE html>

<html class="dark" lang="es"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>KINETIC PULSE - AI Coach</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;700;800&amp;family=Inter:wght@400;600&amp;family=JetBrains+Mono:wght@500&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "on-surface": "#e0e3e6",
                        "on-tertiary-fixed-variant": "#45474c",
                        "on-surface-variant": "#b9caca",
                        "inverse-primary": "#00696e",
                        "on-secondary-fixed": "#1a1c1e",
                        "error-container": "#93000a",
                        "secondary-fixed": "#e2e2e5",
                        "background": "#101416",
                        "primary-container": "#00f5ff",
                        "on-secondary-container": "#b4b5b7",
                        "on-primary-fixed": "#002021",
                        "on-primary-fixed-variant": "#004f53",
                        "outline": "#849495",
                        "surface-bright": "#363a3c",
                        "inverse-surface": "#e0e3e6",
                        "on-primary-container": "#006c71",
                        "on-tertiary": "#2e3035",
                        "tertiary-fixed": "#e2e2e8",
                        "tertiary": "#faf9ff",
                        "surface-container": "#1c2023",
                        "surface-container-low": "#181c1e",
                        "surface-variant": "#323538",
                        "surface-container-highest": "#323538",
                        "secondary-fixed-dim": "#c6c6c9",
                        "on-secondary": "#2f3133",
                        "error": "#ffb4ab",
                        "surface": "#101416",
                        "surface-container-lowest": "#0b0f11",
                        "outline-variant": "#3a494a",
                        "secondary-container": "#454749",
                        "on-tertiary-fixed": "#191c20",
                        "inverse-on-surface": "#2d3133",
                        "primary-fixed-dim": "#00dce5",
                        "on-tertiary-container": "#5f6167",
                        "primary-fixed": "#63f7ff",
                        "secondary": "#c6c6c9",
                        "surface-tint": "#00dce5",
                        "on-primary": "#003739",
                        "primary": "#e9feff",
                        "surface-dim": "#101416",
                        "on-background": "#e0e3e6",
                        "on-error-container": "#ffdad6",
                        "tertiary-container": "#dddde4",
                        "on-secondary-fixed-variant": "#454749",
                        "on-error": "#690005",
                        "tertiary-fixed-dim": "#c5c6cc",
                        "surface-container-high": "#272a2d"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "margin-edge": "16px",
                        "xl": "32px",
                        "lg": "24px",
                        "gutter": "16px",
                        "xs": "4px",
                        "sm": "8px",
                        "md": "16px",
                        "unit": "4px"
                    },
                    "fontFamily": {
                        "display-lg": ["Hanken Grotesk"],
                        "body-md": ["Inter"],
                        "headline-lg": ["Hanken Grotesk"],
                        "title-md": ["Inter"],
                        "label-sm": ["JetBrains Mono"],
                        "headline-lg-mobile": ["Hanken Grotesk"]
                    },
                    "fontSize": {
                        "display-lg": ["48px", { "lineHeight": "56px", "letterSpacing": "-0.02em", "fontWeight": "800" }],
                        "body-md": ["16px", { "lineHeight": "24px", "fontWeight": "400" }],
                        "headline-lg": ["32px", { "lineHeight": "40px", "fontWeight": "700" }],
                        "title-md": ["18px", { "lineHeight": "24px", "fontWeight": "600" }],
                        "label-sm": ["12px", { "lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "500" }],
                        "headline-lg-mobile": ["28px", { "lineHeight": "36px", "fontWeight": "700" }]
                    }
                }
            }
        }
    </script>
<style>
    body {
      min-height: max(884px, 100dvh);
    }
  </style>
  </head>
<body class="bg-background text-on-surface font-body-md text-body-md antialiased min-h-screen pb-24">
<!-- TopAppBar -->
<header class="w-full sticky top-0 z-50 bg-background border-b border-outline-variant flat no shadows flex justify-between items-center px-margin-edge h-16 w-full">
<div class="flex items-center gap-sm">
<div class="w-8 h-8 rounded-full bg-surface-variant flex items-center justify-center overflow-hidden">
<span class="material-symbols-outlined text-primary-container" style="font-variation-settings: 'FILL' 1;">person</span>
</div>
<h1 class="font-headline-lg-mobile text-headline-lg-mobile md:font-headline-lg md:text-headline-lg text-primary-container tracking-tighter">AI Coach</h1>
</div>
<button class="text-primary-container dark:text-primary-container hover:bg-surface-variant transition-colors active:scale-95 transition-transform p-sm rounded-full">
<span class="material-symbols-outlined" data-icon="notifications">notifications</span>
</button>
</header>
<main class="px-margin-edge pt-lg max-w-3xl mx-auto space-y-xl">
<!-- Step 1: Environment -->
<section class="space-y-md">
<h2 class="font-title-md text-title-md text-on-surface">1. Entorno de Entrenamiento</h2>
<div class="grid grid-cols-2 gap-gutter">
<!-- Option 1 -->
<label class="cursor-pointer group">
<input checked="" class="peer sr-only" name="environment" type="radio" value="home_bodyweight"/>
<div class="bg-surface-container rounded-lg p-md border border-outline-variant peer-checked:border-primary-container peer-checked:bg-surface-container-high transition-all">
<span class="material-symbols-outlined text-on-surface-variant peer-checked:text-primary-container mb-sm block text-2xl" style="font-variation-settings: 'FILL' 0;">accessibility_new</span>
<h3 class="font-title-md text-title-md text-on-surface mb-xs">Casa (Calistenia)</h3>
<p class="font-body-md text-body-md text-on-surface-variant text-sm">Peso corporal</p>
</div>
</label>
<!-- Option 2 -->
<label class="cursor-pointer group">
<input class="peer sr-only" name="environment" type="radio" value="home_equipment"/>
<div class="bg-surface-container rounded-lg p-md border border-outline-variant peer-checked:border-primary-container peer-checked:bg-surface-container-high transition-all">
<span class="material-symbols-outlined text-on-surface-variant peer-checked:text-primary-container mb-sm block text-2xl" style="font-variation-settings: 'FILL' 0;">fitness_center</span>
<h3 class="font-title-md text-title-md text-on-surface mb-xs">Casa (Equipo)</h3>
<p class="font-body-md text-body-md text-on-surface-variant text-sm">Mancuernas/Bandas</p>
</div>
</label>
<!-- Option 3 -->
<label class="cursor-pointer group">
<input class="peer sr-only" name="environment" type="radio" value="gym_standard"/>
<div class="bg-surface-container rounded-lg p-md border border-outline-variant peer-checked:border-primary-container peer-checked:bg-surface-container-high transition-all">
<span class="material-symbols-outlined text-on-surface-variant peer-checked:text-primary-container mb-sm block text-2xl" style="font-variation-settings: 'FILL' 0;">domain</span>
<h3 class="font-title-md text-title-md text-on-surface mb-xs">Gimnasio Estándar</h3>
<p class="font-body-md text-body-md text-on-surface-variant text-sm">Equipamiento completo</p>
</div>
</label>
<!-- Option 4 -->
<label class="cursor-pointer group col-span-2">
<input class="peer sr-only" id="gymChainRadio" name="environment" type="radio" value="gym_chain"/>
<div class="bg-surface-container rounded-lg p-md border border-outline-variant peer-checked:border-primary-container peer-checked:bg-surface-container-high transition-all">
<div class="flex items-center gap-sm mb-md">
<span class="material-symbols-outlined text-on-surface-variant peer-checked:text-primary-container text-2xl" style="font-variation-settings: 'FILL' 0;">search</span>
<div>
<h3 class="font-title-md text-title-md text-on-surface">Gimnasio de Cadena</h3>
</div>
</div>
<!-- Dynamic Search Bar (shown when selected) -->
<div class="mt-sm hidden peer-checked:block transition-all">
<div class="relative">
<span class="material-symbols-outlined absolute left-sm top-1/2 -translate-y-1/2 text-on-surface-variant text-lg">search</span>
<input class="w-full bg-surface-variant text-on-surface placeholder-on-surface-variant border-b border-outline focus:border-primary-container focus:ring-0 rounded-t px-10 py-sm font-body-md text-body-md" placeholder="Buscar sede o inventario de máquinas..." type="text"/>
</div>
</div>
</div>
</label>
</div>
</section>
<!-- Step 2: Goal -->
<section class="space-y-md">
<h2 class="font-title-md text-title-md text-on-surface">2. Tu Objetivo</h2>
<div class="flex flex-wrap gap-sm">
<!-- Chip 1 -->
<label class="cursor-pointer">
<input checked="" class="peer sr-only" name="goal" type="radio" value="hypertrophy"/>
<div class="px-md py-sm rounded-full border border-outline-variant bg-surface-container text-on-surface-variant peer-checked:bg-primary-container peer-checked:text-on-primary-container peer-checked:border-primary-container transition-colors font-label-sm text-label-sm uppercase">
                        Hipertrofia
                    </div>
</label>
<!-- Chip 2 -->
<label class="cursor-pointer">
<input class="peer sr-only" name="goal" type="radio" value="fat_loss"/>
<div class="px-md py-sm rounded-full border border-outline-variant bg-surface-container text-on-surface-variant peer-checked:bg-primary-container peer-checked:text-on-primary-container peer-checked:border-primary-container transition-colors font-label-sm text-label-sm uppercase">
                        Pérdida de grasa
                    </div>
</label>
<!-- Chip 3 -->
<label class="cursor-pointer">
<input class="peer sr-only" name="goal" type="radio" value="recomp"/>
<div class="px-md py-sm rounded-full border border-outline-variant bg-surface-container text-on-surface-variant peer-checked:bg-primary-container peer-checked:text-on-primary-container peer-checked:border-primary-container transition-colors font-label-sm text-label-sm uppercase">
                        Recomposición
                    </div>
</label>
<!-- Chip 4 -->
<label class="cursor-pointer">
<input class="peer sr-only" name="goal" type="radio" value="strength"/>
<div class="px-md py-sm rounded-full border border-outline-variant bg-surface-container text-on-surface-variant peer-checked:bg-primary-container peer-checked:text-on-primary-container peer-checked:border-primary-container transition-colors font-label-sm text-label-sm uppercase">
                        Fuerza
                    </div>
</label>
</div>
</section>
<!-- Action Area -->
<section class="pt-md">
<button class="w-full bg-primary-container text-on-primary-fixed rounded-lg py-md px-lg font-label-sm text-label-sm uppercase font-bold flex items-center justify-center gap-sm active:scale-95 transition-transform hover:brightness-110">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">auto_awesome</span>
                Generar Plan Personalizado
            </button>
</section>
<!-- Proposal Preview -->
<section class="mt-lg border border-outline-variant border-dashed rounded-lg p-xl flex flex-col items-center justify-center text-center bg-surface-container-low min-h-[200px]">
<span class="material-symbols-outlined text-on-surface-variant text-4xl mb-sm opacity-50" style="font-variation-settings: 'FILL' 0;">article</span>
<p class="font-body-md text-body-md text-on-surface-variant">Tu rutina aparecerá aquí...</p>
</section>
</main>
<!-- BottomNavBar -->
<nav class="fixed bottom-0 w-full z-50 rounded-t-xl bg-surface-container dark:bg-surface-container border-t border-outline-variant shadow-lg fixed bottom-0 left-0 w-full flex justify-around items-center py-sm px-md pb-safe md:hidden">
<a class="flex flex-col items-center justify-center text-on-surface-variant hover:text-on-surface transition-colors active:scale-90 transition-all duration-200 p-sm group" href="#">
<span class="material-symbols-outlined mb-xs group-hover:text-primary-container transition-colors" data-icon="home">home</span>
<span class="font-label-sm text-label-sm">Home</span>
</a>
<a class="flex flex-col items-center justify-center bg-primary-container text-on-primary-container rounded-full px-4 py-1 hover:text-on-surface transition-colors active:scale-90 transition-all duration-200" href="#">
<span class="material-symbols-outlined mb-xs" data-icon="fitness_center" style="font-variation-settings: 'FILL' 1;">fitness_center</span>
<span class="font-label-sm text-label-sm">Workouts</span>
</a>
<a class="flex flex-col items-center justify-center text-on-surface-variant hover:text-on-surface transition-colors active:scale-90 transition-all duration-200 p-sm group" href="#">
<span class="material-symbols-outlined mb-xs group-hover:text-primary-container transition-colors" data-icon="insights">insights</span>
<span class="font-label-sm text-label-sm">Progress</span>
</a>
<a class="flex flex-col items-center justify-center text-on-surface-variant hover:text-on-surface transition-colors active:scale-90 transition-all duration-200 p-sm group" href="#">
<span class="material-symbols-outlined mb-xs group-hover:text-primary-container transition-colors" data-icon="person">person</span>
<span class="font-label-sm text-label-sm">Profile</span>
</a>
</nav>
<!-- Script to toggle search bar visibility if radio changes (fallback for older browsers, though peer-checked handles CSS mostly) -->
<script>
        const gymChainRadio = document.getElementById('gymChainRadio');
        const radios = document.querySelectorAll('input[name="environment"]');
        
        radios.forEach(radio => {
            radio.addEventListener('change', (e) => {
                // CSS peer-checked handles the visual toggle, this is just for accessibility/focus if needed later
                if(e.target.value === 'gym_chain') {
                    const searchInput = e.target.closest('label').querySelector('input[type="text"]');
                    if(searchInput) setTimeout(() => searchInput.focus(), 100);
                }
            });
        });
    </script>
</body></html>
