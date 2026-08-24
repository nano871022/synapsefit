<!DOCTYPE html>

<html class="dark" lang="en"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0" name="viewport"/>
<title>Kinetic Pulse - Active Workout</title>
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;700;800&amp;family=JetBrains+Mono:wght@500&amp;family=Inter:wght@400;600&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "primary-container": "#00f5ff",
                        "tertiary": "#faf9ff",
                        "error-container": "#93000a",
                        "on-primary-container": "#006c71",
                        "inverse-on-surface": "#2d3133",
                        "on-tertiary-fixed-variant": "#45474c",
                        "outline-variant": "#3a494a",
                        "on-surface-variant": "#b9caca",
                        "tertiary-fixed": "#e2e2e8",
                        "on-secondary": "#2f3133",
                        "tertiary-fixed-dim": "#c5c6cc",
                        "on-surface": "#e0e3e6",
                        "on-primary": "#003739",
                        "inverse-primary": "#00696e",
                        "surface": "#101416",
                        "surface-container-low": "#181c1e",
                        "on-background": "#e0e3e6",
                        "error": "#ffb4ab",
                        "primary-fixed-dim": "#00dce5",
                        "inverse-surface": "#e0e3e6",
                        "on-primary-fixed": "#002021",
                        "secondary-fixed-dim": "#c6c6c9",
                        "secondary-fixed": "#e2e2e5",
                        "primary-fixed": "#63f7ff",
                        "surface-dim": "#101416",
                        "on-secondary-fixed": "#1a1c1e",
                        "on-error-container": "#ffdad6",
                        "secondary-container": "#454749",
                        "tertiary-container": "#dddde4",
                        "on-error": "#690005",
                        "secondary": "#c6c6c9",
                        "outline": "#849495",
                        "surface-container-lowest": "#0b0f11",
                        "on-tertiary": "#2e3035",
                        "surface-container-highest": "#323538",
                        "on-secondary-container": "#b4b5b7",
                        "background": "#101416",
                        "on-primary-fixed-variant": "#004f53",
                        "on-secondary-fixed-variant": "#454749",
                        "surface-variant": "#323538",
                        "on-tertiary-fixed": "#191c20",
                        "surface-container-high": "#272a2d",
                        "primary": "#e9feff",
                        "on-tertiary-container": "#5f6167",
                        "surface-tint": "#00dce5",
                        "surface-container": "#1c2023",
                        "surface-bright": "#363a3c"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "sm": "8px",
                        "unit": "4px",
                        "md": "16px",
                        "gutter": "16px",
                        "margin-edge": "16px",
                        "xs": "4px",
                        "xl": "32px",
                        "lg": "24px"
                    },
                    "fontFamily": {
                        "headline-lg-mobile": ["Hanken Grotesk"],
                        "display-lg": ["Hanken Grotesk"],
                        "label-sm": ["JetBrains Mono"],
                        "headline-lg": ["Hanken Grotesk"],
                        "title-md": ["Inter"],
                        "body-md": ["Inter"]
                    },
                    "fontSize": {
                        "headline-lg-mobile": ["28px", { "lineHeight": "36px", "fontWeight": "700" }],
                        "display-lg": ["48px", { "lineHeight": "56px", "letterSpacing": "-0.02em", "fontWeight": "800" }],
                        "label-sm": ["12px", { "lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "500" }],
                        "headline-lg": ["32px", { "lineHeight": "40px", "fontWeight": "700" }],
                        "title-md": ["18px", { "lineHeight": "24px", "fontWeight": "600" }],
                        "body-md": ["16px", { "lineHeight": "24px", "fontWeight": "400" }]
                    }
                }
            }
        }
    </script>
<style>
        .pulse-anim {
            animation: pulse 1.5s infinite;
        }
        @keyframes pulse {
            0% { transform: scale(1); opacity: 1; }
            50% { transform: scale(1.1); opacity: 0.7; }
            100% { transform: scale(1); opacity: 1; }
        }
        .neon-glow {
            text-shadow: 0 0 10px rgba(0, 245, 255, 0.5), 0 0 20px rgba(0, 245, 255, 0.3);
        }
        .glow-button {
            box-shadow: 0 0 10px rgba(0, 245, 255, 0.4);
        }
    </style>
<style>
    body {
      min-height: max(884px, 100dvh);
    }
  </style>
  </head>
<body class="bg-surface text-on-surface min-h-screen pb-24 pt-16">
<!-- TopAppBar -->
<header class="fixed top-0 w-full z-50 flex items-center justify-between px-margin-edge h-16 bg-surface border-b border-outline-variant flat no shadows">
<button class="text-on-surface-variant hover:bg-surface-container-high transition-colors active:scale-95 duration-100 p-2 rounded-full flex items-center justify-center">
<span class="material-symbols-outlined">close</span>
</button>
<h1 class="font-headline-lg-mobile text-headline-lg-mobile font-black text-primary-container tracking-tighter">KINETIC PULSE</h1>
<button class="text-on-surface-variant hover:bg-surface-container-high transition-colors active:scale-95 duration-100 p-2 rounded-full flex items-center justify-center">
<span class="material-symbols-outlined">settings</span>
</button>
</header>
<main class="px-margin-edge flex flex-col gap-gutter mt-lg max-w-2xl mx-auto">
<!-- Header Section (Timer & HR) -->
<section class="flex flex-col items-center justify-center py-xl">
<div class="flex items-center gap-md">
<h2 class="font-display-lg text-display-lg text-primary-container neon-glow font-label-sm">00:42:15</h2>
</div>
<div class="flex items-center gap-sm mt-sm text-error">
<span class="material-symbols-outlined pulse-anim" style="font-variation-settings: 'FILL' 1;">favorite</span>
<span class="font-label-sm text-label-sm text-on-surface-variant">132 BPM</span>
</div>
</section>
<!-- Rest Timer -->
<section class="bg-surface-container-low rounded-xl p-md flex items-center justify-between border border-outline-variant/30">
<div class="flex items-center gap-sm">
<span class="material-symbols-outlined text-primary-container">timer</span>
<span class="font-title-md text-title-md">Rest Timer</span>
</div>
<div class="w-12 h-12 rounded-full border-2 border-primary-container flex items-center justify-center relative glow-button">
<span class="font-label-sm text-label-sm text-primary-container">55s</span>
</div>
</section>
<!-- Progress -->
<div class="flex justify-between items-center px-sm">
<span class="font-label-sm text-label-sm text-on-surface-variant uppercase">Exercise 2 of 8</span>
<div class="flex gap-1">
<div class="w-2 h-2 rounded-full bg-primary-container"></div>
<div class="w-2 h-2 rounded-full bg-primary-container"></div>
<div class="w-2 h-2 rounded-full bg-surface-container-high"></div>
<div class="w-2 h-2 rounded-full bg-surface-container-high"></div>
</div>
</div>
<!-- Exercise Focus & Set Tracking -->
<section class="bg-surface-container rounded-xl p-md flex flex-col gap-md border border-outline-variant/20">
<div class="flex justify-between items-center mb-sm">
<h3 class="font-headline-lg-mobile text-headline-lg-mobile text-primary-fixed-dim">Bench Press</h3>
<button class="text-on-surface-variant hover:text-primary-container transition-colors">
<span class="material-symbols-outlined">more_horiz</span>
</button>
</div>
<!-- Sets Header -->
<div class="grid grid-cols-5 gap-sm font-label-sm text-label-sm text-on-surface-variant uppercase px-xs">
<div class="text-center">Set</div>
<div class="col-span-1 text-center">Prev</div>
<div class="text-center">kg</div>
<div class="text-center">Reps</div>
<div class="text-center">Done</div>
</div>
<!-- Set 1 (Completed) -->
<div class="grid grid-cols-5 gap-sm items-center bg-surface-variant/30 p-2 rounded-lg">
<div class="text-center font-label-sm text-label-sm text-on-surface-variant">1</div>
<div class="col-span-1 text-center font-label-sm text-label-sm text-on-surface-variant/70">60x10</div>
<input class="bg-surface-container-highest border-b border-primary-container text-center text-on-surface font-title-md rounded-none px-1 py-1 focus:ring-0 w-full" disabled="" type="text" value="60"/>
<input class="bg-surface-container-highest border-b border-primary-container text-center text-on-surface font-title-md rounded-none px-1 py-1 focus:ring-0 w-full" disabled="" type="text" value="10"/>
<button class="mx-auto w-8 h-8 rounded bg-primary-container/20 text-primary-container flex items-center justify-center">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">check</span>
</button>
</div>
<!-- Set 2 (Active) -->
<div class="grid grid-cols-5 gap-sm items-center bg-surface-container-high p-2 rounded-lg border-l-2 border-primary-container">
<div class="text-center font-label-sm text-label-sm text-primary-container">2</div>
<div class="col-span-1 text-center font-label-sm text-label-sm text-on-surface-variant/70">65x8</div>
<input class="bg-surface border-b border-outline-variant text-center text-on-surface font-title-md rounded-none px-1 py-1 focus:ring-0 focus:border-primary-container w-full transition-colors" type="text" value="65"/>
<input class="bg-surface border-b border-outline-variant text-center text-on-surface font-title-md rounded-none px-1 py-1 focus:ring-0 focus:border-primary-container w-full transition-colors" type="text" value="8"/>
<button class="mx-auto w-8 h-8 rounded bg-surface-container-lowest border border-outline-variant text-on-surface-variant hover:border-primary-container hover:text-primary-container transition-colors flex items-center justify-center">
<span class="material-symbols-outlined">check</span>
</button>
</div>
<!-- Set 3 (Pending) -->
<div class="grid grid-cols-5 gap-sm items-center p-2 rounded-lg opacity-60">
<div class="text-center font-label-sm text-label-sm text-on-surface-variant">3</div>
<div class="col-span-1 text-center font-label-sm text-label-sm text-on-surface-variant/70">65x8</div>
<input class="bg-surface-container border-b border-outline-variant/50 text-center text-on-surface font-title-md rounded-none px-1 py-1 focus:ring-0 focus:border-primary-container w-full" placeholder="-" type="text"/>
<input class="bg-surface-container border-b border-outline-variant/50 text-center text-on-surface font-title-md rounded-none px-1 py-1 focus:ring-0 focus:border-primary-container w-full" placeholder="-" type="text"/>
<button class="mx-auto w-8 h-8 rounded bg-surface-container-lowest border border-outline-variant/50 text-on-surface-variant/50 flex items-center justify-center">
<span class="material-symbols-outlined">check</span>
</button>
</div>
<button class="mt-sm w-full py-2 flex items-center justify-center gap-2 text-on-surface-variant hover:text-primary-container hover:bg-surface-container-high rounded-lg transition-colors font-label-sm uppercase">
<span class="material-symbols-outlined text-[18px]">add</span> Add Set
            </button>
</section>
<!-- Primary Action -->
<button class="w-full bg-primary-container text-on-primary-container py-3 rounded-lg font-label-sm text-label-sm uppercase tracking-wider font-bold hover:opacity-90 active:scale-95 transition-all mt-md shadow-[0_0_15px_rgba(0,245,255,0.3)]">
            Complete Exercise
        </button>
</main>
<!-- BottomNavBar -->
<nav class="fixed bottom-0 w-full z-50 flex justify-around items-center px-4 py-2 bg-surface-container-low border-t border-outline-variant docked full-width rounded-t-xl md:hidden">
<button class="flex flex-col items-center justify-center bg-primary-container text-on-primary-container rounded-full px-4 py-1 active:scale-90 duration-200">
<span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">fitness_center</span>
<span class="font-label-sm text-label-sm mt-1">WORKOUT</span>
</button>
<button class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary-fixed-dim transition-all active:scale-90 duration-200">
<span class="material-symbols-outlined">history</span>
<span class="font-label-sm text-label-sm mt-1">HISTORY</span>
</button>
<button class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary-fixed-dim transition-all active:scale-90 duration-200">
<span class="material-symbols-outlined">insights</span>
<span class="font-label-sm text-label-sm mt-1">METRICS</span>
</button>
<button class="flex flex-col items-center justify-center text-on-surface-variant hover:text-primary-fixed-dim transition-all active:scale-90 duration-200">
<span class="material-symbols-outlined">person</span>
<span class="font-label-sm text-label-sm mt-1">PROFILE</span>
</button>
</nav>
</body></html>
