<!DOCTYPE html>

<html class="dark" lang="es"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>LLM Settings - Kinetic Pulse</title>
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
                        "surface-container-highest": "#323538",
                        "on-tertiary": "#2e3035",
                        "surface-container-high": "#272a2d",
                        "surface-variant": "#323538",
                        "on-error-container": "#ffdad6",
                        "secondary-container": "#454749",
                        "inverse-surface": "#e0e3e6",
                        "on-tertiary-container": "#5f6167",
                        "error-container": "#93000a",
                        "secondary-fixed": "#e2e2e5",
                        "tertiary-fixed-dim": "#c5c6cc",
                        "background": "#101416",
                        "tertiary": "#faf9ff",
                        "on-secondary-container": "#b4b5b7",
                        "on-primary": "#003739",
                        "tertiary-fixed": "#e2e2e8",
                        "on-primary-fixed": "#002021",
                        "secondary": "#c6c6c9",
                        "secondary-fixed-dim": "#c6c6c9",
                        "tertiary-container": "#dddde4",
                        "surface-container-lowest": "#0b0f11",
                        "surface-container": "#1c2023",
                        "primary-container": "#00f5ff",
                        "error": "#ffb4ab",
                        "inverse-primary": "#00696e",
                        "on-tertiary-fixed-variant": "#45474c",
                        "on-background": "#e0e3e6",
                        "on-surface-variant": "#b9caca",
                        "on-primary-container": "#006c71",
                        "surface-tint": "#00dce5",
                        "surface": "#101416",
                        "surface-dim": "#101416",
                        "on-primary-fixed-variant": "#004f53",
                        "on-secondary-fixed": "#1a1c1e",
                        "inverse-on-surface": "#2d3133",
                        "primary-fixed": "#63f7ff",
                        "outline": "#849495",
                        "on-tertiary-fixed": "#191c20",
                        "primary-fixed-dim": "#00dce5",
                        "primary": "#e9feff",
                        "on-secondary": "#2f3133",
                        "outline-variant": "#3a494a",
                        "on-error": "#690005",
                        "on-secondary-fixed-variant": "#454749",
                        "on-surface": "#e0e3e6",
                        "surface-bright": "#363a3c",
                        "surface-container-low": "#181c1e"
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
                        "xs": "4px",
                        "gutter": "16px",
                        "lg": "24px",
                        "sm": "8px",
                        "md": "16px",
                        "unit": "4px"
                    },
                    "fontFamily": {
                        "display-lg": ["Hanken Grotesk"],
                        "title-md": ["Inter"],
                        "body-md": ["Inter"],
                        "label-sm": ["JetBrains Mono"],
                        "headline-lg-mobile": ["Hanken Grotesk"],
                        "headline-lg": ["Hanken Grotesk"]
                    },
                    "fontSize": {
                        "display-lg": ["48px", { "lineHeight": "56px", "letterSpacing": "-0.02em", "fontWeight": "800" }],
                        "title-md": ["18px", { "lineHeight": "24px", "fontWeight": "600" }],
                        "body-md": ["16px", { "lineHeight": "24px", "fontWeight": "400" }],
                        "label-sm": ["12px", { "lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "500" }],
                        "headline-lg-mobile": ["28px", { "lineHeight": "36px", "fontWeight": "700" }],
                        "headline-lg": ["32px", { "lineHeight": "40px", "fontWeight": "700" }]
                    }
                }
            }
        }
    </script>
<style>
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 1;
        }
    </style>
<style>
    body {
      min-height: max(884px, 100dvh);
    }
  </style>
  </head>
<body class="bg-background text-on-background min-h-screen pb-[80px]">
<!-- TopAppBar -->
<header class="w-full top-0 sticky bg-background border-b border-white/10 z-50">
<div class="flex justify-between items-center px-margin-edge h-16 w-full">
<div class="flex items-center gap-4">
<div class="w-8 h-8 rounded-full bg-surface-container-highest overflow-hidden">
<img class="w-full h-full object-cover" data-alt="A futuristic fitness avatar, cyberpunk aesthetic, neon blue accents against dark carbon fiber textures, representing a high-tech corporate health persona." src="https://lh3.googleusercontent.com/aida-public/AB6AXuDlOgdug6xUdKmf5idwn8uED_NvclWWhn74kWQSGJ2KiRyzpMkz6fBHhf192Y-x57ib1xpkqB1Y7lwxmizG7LpelPkNf3Umy0x33v4gp0Ou8gkWQjy171BaMaIteHuICniuTypZSU6UxOeut5MZoukFydcWWTpRCfq7h6KbgYewLbRoRb0ZJN-9uLX73Be-x37s0NiWt-fV69EmiIGZQfX8QLZO0BQBaAsxD4HsyU1tP7Ubb0k5F6JR"/>
</div>
<h1 class="font-display-lg text-display-lg text-primary-fixed-dim tracking-tighter text-[24px] leading-tight">Kinetic Pulse</h1>
</div>
<button class="text-on-surface-variant hover:bg-surface-container-highest transition-colors rounded-full p-2 active:scale-95 duration-150">
<span class="material-symbols-outlined">notifications</span>
</button>
</div>
</header>
<!-- Main Content -->
<main class="p-margin-edge max-w-4xl mx-auto space-y-gutter">
<!-- Page Header -->
<div class="mb-xl">
<h2 class="font-headline-lg-mobile text-headline-lg-mobile text-primary mb-2">Administración de Proveedores IA</h2>
<div class="flex items-center gap-2 text-primary-fixed-dim bg-primary-fixed-dim/10 w-fit px-3 py-1 rounded-full border border-primary-fixed-dim/20">
<span class="material-symbols-outlined text-[16px]">lock</span>
<span class="font-label-sm text-label-sm uppercase">Cifrado Local Activo</span>
</div>
</div>
<!-- Provider Cards -->
<!-- Gemini Card -->
<section class="bg-surface-container rounded-xl p-md border border-white/10 relative overflow-hidden">
<div class="absolute top-0 left-0 w-1 h-full bg-primary-container"></div>
<div class="flex justify-between items-start mb-md">
<div class="flex items-center gap-3">
<div class="w-10 h-10 rounded-lg bg-surface-container-highest flex items-center justify-center border border-white/5">
<span class="material-symbols-outlined text-primary-fixed-dim">psychiatry</span>
</div>
<div>
<h3 class="font-title-md text-title-md text-on-surface">Gemini</h3>
<p class="font-body-md text-body-md text-on-surface-variant text-sm">Google AI Studio</p>
</div>
</div>
<div class="flex items-center gap-3">
<span class="bg-primary-container text-on-primary-container font-label-sm text-label-sm uppercase px-2 py-1 rounded">Prioridad de Respuesta</span>
<label class="relative inline-flex items-center cursor-pointer">
<input checked="" class="sr-only peer" type="checkbox" value=""/>
<div class="w-11 h-6 bg-surface-container-highest peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-container"></div>
</label>
</div>
</div>
<div class="space-y-4">
<div>
<label class="block font-label-sm text-label-sm text-on-surface-variant uppercase mb-1">API Key</label>
<div class="flex gap-2">
<input class="w-full bg-surface-container-highest border-none rounded-lg text-on-surface font-body-md focus:ring-1 focus:ring-primary-container" disabled="" type="password" value="************************"/>
<button class="bg-surface-container-highest border border-white/10 text-on-surface px-4 rounded-lg font-label-sm uppercase hover:bg-surface-container-high transition-colors">Edit</button>
</div>
</div>
<div>
<label class="block font-label-sm text-label-sm text-on-surface-variant uppercase mb-1">Model Selection</label>
<select class="w-full bg-surface-container-highest border-none rounded-lg text-on-surface font-body-md focus:ring-1 focus:ring-primary-container">
<option>Gemini 1.5 Pro</option>
<option>Gemini 1.5 Flash</option>
</select>
</div>
</div>
</section>
<!-- OpenAI Card -->
<section class="bg-surface-container rounded-xl p-md border border-white/10">
<div class="flex justify-between items-start mb-md">
<div class="flex items-center gap-3">
<div class="w-10 h-10 rounded-lg bg-surface-container-highest flex items-center justify-center border border-white/5">
<span class="material-symbols-outlined text-on-surface-variant">memory</span>
</div>
<div>
<h3 class="font-title-md text-title-md text-on-surface">OpenAI</h3>
<p class="font-body-md text-body-md text-on-surface-variant text-sm">Platform API</p>
</div>
</div>
<div class="flex items-center gap-3">
<label class="relative inline-flex items-center cursor-pointer">
<input class="sr-only peer" type="checkbox" value=""/>
<div class="w-11 h-6 bg-surface-container-highest peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-on-surface-variant after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-container"></div>
</label>
</div>
</div>
<div class="space-y-4 opacity-50">
<div>
<label class="block font-label-sm text-label-sm text-on-surface-variant uppercase mb-1">API Key</label>
<div class="flex gap-2">
<input class="w-full bg-surface-container-highest border-none rounded-lg text-on-surface font-body-md focus:ring-1 focus:ring-primary-container" disabled="" placeholder="Enter API Key" type="password"/>
<button class="bg-primary-container text-on-primary-container px-4 rounded-lg font-label-sm uppercase hover:bg-primary-fixed-dim transition-colors" disabled="">Save</button>
</div>
</div>
<div>
<label class="block font-label-sm text-label-sm text-on-surface-variant uppercase mb-1">Model Selection</label>
<select class="w-full bg-surface-container-highest border-none rounded-lg text-on-surface font-body-md focus:ring-1 focus:ring-primary-container" disabled="">
<option>GPT-4o</option>
<option>GPT-4-Turbo</option>
</select>
</div>
</div>
</section>
<!-- Anthropic Card -->
<section class="bg-surface-container rounded-xl p-md border border-white/10">
<div class="flex justify-between items-start mb-md">
<div class="flex items-center gap-3">
<div class="w-10 h-10 rounded-lg bg-surface-container-highest flex items-center justify-center border border-white/5">
<span class="material-symbols-outlined text-on-surface-variant">forum</span>
</div>
<div>
<h3 class="font-title-md text-title-md text-on-surface">Anthropic</h3>
<p class="font-body-md text-body-md text-on-surface-variant text-sm">Console API</p>
</div>
</div>
<div class="flex items-center gap-3">
<label class="relative inline-flex items-center cursor-pointer">
<input class="sr-only peer" type="checkbox" value=""/>
<div class="w-11 h-6 bg-surface-container-highest peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-on-surface-variant after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-container"></div>
</label>
</div>
</div>
<div class="space-y-4 opacity-50">
<div>
<label class="block font-label-sm text-label-sm text-on-surface-variant uppercase mb-1">API Key</label>
<div class="flex gap-2">
<input class="w-full bg-surface-container-highest border-none rounded-lg text-on-surface font-body-md focus:ring-1 focus:ring-primary-container" disabled="" placeholder="Enter API Key" type="password"/>
<button class="bg-primary-container text-on-primary-container px-4 rounded-lg font-label-sm uppercase hover:bg-primary-fixed-dim transition-colors" disabled="">Save</button>
</div>
</div>
<div>
<label class="block font-label-sm text-label-sm text-on-surface-variant uppercase mb-1">Model Selection</label>
<select class="w-full bg-surface-container-highest border-none rounded-lg text-on-surface font-body-md focus:ring-1 focus:ring-primary-container" disabled="">
<option>Claude 3.5 Sonnet</option>
<option>Claude 3 Opus</option>
</select>
</div>
</div>
</section>
</main>
<!-- BottomNavBar -->
<!-- Suppressed based on logic: Settings/Sub-view typically hides bottom nav for focus, but prompt requests it with Profile highlighted if applicable. Rendering as requested, but conceptually it might be hidden in a pure transactional setting. -->
<nav class="fixed bottom-0 left-0 w-full flex justify-around items-center px-4 py-3 pb-safe bg-surface-container-lowest border-t border-white/10 z-50 rounded-t-xl md:hidden shadow-lg">
<a class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary transition-all active:scale-90 duration-200" href="#">
<span class="material-symbols-outlined mb-1">home</span>
<span class="font-label-sm text-label-sm uppercase">Home</span>
</a>
<a class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary transition-all active:scale-90 duration-200" href="#">
<span class="material-symbols-outlined mb-1">fitness_center</span>
<span class="font-label-sm text-label-sm uppercase">Workouts</span>
</a>
<a class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary transition-all active:scale-90 duration-200" href="#">
<span class="material-symbols-outlined mb-1">insights</span>
<span class="font-label-sm text-label-sm uppercase">Progress</span>
</a>
<a class="flex flex-col items-center justify-center bg-primary-container text-on-primary-container rounded-full px-4 py-1 hover:text-primary transition-all active:scale-90 duration-200" href="#">
<span class="material-symbols-outlined mb-1">person</span>
<span class="font-label-sm text-label-sm uppercase">Profile</span>
</a>
</nav>
</body></html>
