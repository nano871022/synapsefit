<!DOCTYPE html>

<html class="dark" lang="en"><head>
<meta charset="utf-8"/>
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport"/>
<title>Kinetic Pulse - Wear OS Active Workout</title>
<!-- Material Symbols -->
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<!-- Google Fonts: Inter, Hanken Grotesk, JetBrains Mono -->
<link href="https://fonts.googleapis.com" rel="preconnect"/>
<link crossorigin="" href="https://fonts.gstatic.com" rel="preconnect"/>
<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@700;800&amp;family=Inter:wght@400;600&amp;family=JetBrains+Mono:wght@500&amp;display=swap" rel="stylesheet"/>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&amp;display=swap" rel="stylesheet"/>
<!-- Tailwind CSS -->
<script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
<!-- Tailwind Configuration -->
<script id="tailwind-config">
      tailwind.config = {
        darkMode: "class",
        theme: {
          extend: {
            "colors": {
                    "tertiary-container": "#dddde4",
                    "outline-variant": "#3a494a",
                    "on-error": "#690005",
                    "on-primary": "#003739",
                    "on-error-container": "#ffdad6",
                    "on-tertiary-container": "#5f6167",
                    "surface-tint": "#00dce5",
                    "on-background": "#e0e3e6",
                    "surface-container-lowest": "#0b0f11",
                    "on-tertiary": "#2e3035",
                    "secondary-container": "#454749",
                    "inverse-on-surface": "#2d3133",
                    "surface-container-high": "#272a2d",
                    "primary": "#e9feff",
                    "inverse-primary": "#00696e",
                    "background": "#101416",
                    "on-tertiary-fixed-variant": "#45474c",
                    "error": "#ffb4ab",
                    "primary-fixed-dim": "#00dce5",
                    "on-primary-fixed": "#002021",
                    "error-container": "#93000a",
                    "on-secondary-container": "#b4b5b7",
                    "on-surface": "#e0e3e6",
                    "surface": "#101416",
                    "surface-bright": "#363a3c",
                    "primary-container": "#00f5ff",
                    "secondary-fixed": "#e2e2e5",
                    "secondary": "#c6c6c9",
                    "tertiary-fixed": "#e2e2e8",
                    "secondary-fixed-dim": "#c6c6c9",
                    "primary-fixed": "#63f7ff",
                    "surface-container-low": "#181c1e",
                    "outline": "#849495",
                    "on-secondary": "#2f3133",
                    "surface-dim": "#101416",
                    "inverse-surface": "#e0e3e6",
                    "tertiary": "#faf9ff",
                    "on-primary-container": "#006c71",
                    "on-primary-fixed-variant": "#004f53",
                    "surface-container-highest": "#323538",
                    "on-tertiary-fixed": "#191c20",
                    "surface-container": "#1c2023",
                    "on-secondary-fixed-variant": "#454749",
                    "on-surface-variant": "#b9caca",
                    "on-secondary-fixed": "#1a1c1e",
                    "surface-variant": "#323538",
                    "tertiary-fixed-dim": "#c5c6cc"
            },
            "borderRadius": {
                    "DEFAULT": "0.25rem",
                    "lg": "0.5rem",
                    "xl": "0.75rem",
                    "full": "9999px"
            },
            "spacing": {
                    "xs": "4px",
                    "margin-edge": "16px",
                    "md": "16px",
                    "lg": "24px",
                    "gutter": "16px",
                    "xl": "32px",
                    "sm": "8px",
                    "unit": "4px"
            },
            "fontFamily": {
                    "title-md": ["Inter"],
                    "headline-lg-mobile": ["Hanken Grotesk"],
                    "headline-lg": ["Hanken Grotesk"],
                    "label-sm": ["JetBrains Mono"],
                    "body-md": ["Inter"],
                    "display-lg": ["Hanken Grotesk"]
            },
            "fontSize": {
                    "title-md": ["18px", {"lineHeight": "24px", "fontWeight": "600"}],
                    "headline-lg-mobile": ["28px", {"lineHeight": "36px", "fontWeight": "700"}],
                    "headline-lg": ["32px", {"lineHeight": "40px", "fontWeight": "700"}],
                    "label-sm": ["12px", {"lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "500"}],
                    "body-md": ["16px", {"lineHeight": "24px", "fontWeight": "400"}],
                    "display-lg": ["48px", {"lineHeight": "56px", "letterSpacing": "-0.02em", "fontWeight": "800"}]
            }
          },
        }
      }
    </script>
<style>
        /* Pulse Animation for Heart */
        @keyframes heartbeat {
            0%, 100% { transform: scale(1); opacity: 1; }
            50% { transform: scale(1.15); opacity: 0.8; }
        }
        .animate-heartbeat {
            animation: heartbeat 1s infinite ease-in-out;
        }
        
        /* Wear OS Circular Constraint Simulation for Web View */
        .wear-os-screen {
            width: 384px;
            height: 384px;
            border-radius: 50%;
            overflow: hidden;
            position: relative;
            background-color: #0b0f11; /* surface-container-lowest */
            box-shadow: inset 0 0 40px rgba(0,0,0,0.8);
            margin: 0 auto;
        }
        
        /* Arc Text Simulation (Basic positioning for this context) */
        .arc-text-container {
            position: absolute;
            top: 24px;
            width: 100%;
            text-align: center;
        }
    </style>
<style>
    body {
      min-height: max(884px, 100dvh);
    }
  </style>
  </head>
<body class="bg-background text-on-surface flex items-center justify-center min-h-screen">
<!-- Wear OS Simulated Circular Display -->
<main class="wear-os-screen flex flex-col items-center justify-between py-8 px-6">
<!-- Top Arc: Exercise Name -->
<div class="arc-text-container">
<h1 class="font-label-sm text-label-sm text-primary uppercase tracking-widest">Bench Press</h1>
</div>
<!-- Sync Status Icon -->
<div class="absolute top-[60px] flex items-center justify-center bg-surface-container-high/50 rounded-full p-1" title="Synced">
<span class="material-symbols-outlined text-[14px] text-primary" style="font-variation-settings: 'FILL' 1;">cloud_sync</span>
</div>
<!-- Central Data: Heart Rate -->
<div class="flex flex-col items-center justify-center mt-12 mb-4 w-full">
<div class="flex items-end justify-center space-x-2">
<span class="material-symbols-outlined animate-heartbeat text-primary-container text-[32px]" style="font-variation-settings: 'FILL' 1;">favorite</span>
<span class="font-display-lg text-display-lg text-primary leading-none">142</span>
</div>
<span class="font-label-sm text-label-sm text-outline-variant uppercase mt-1">BPM</span>
</div>
<!-- Rep Counter Module -->
<div class="flex items-center justify-between w-full max-w-[200px] bg-surface-container-low rounded-full px-2 py-1 border border-outline-variant/30">
<!-- Minus Button -->
<button aria-label="Decrease reps" class="w-12 h-12 flex items-center justify-center rounded-full bg-surface-variant text-on-surface active:scale-90 transition-transform hover:bg-surface-container-high">
<span class="material-symbols-outlined text-[24px]">remove</span>
</button>
<!-- Current Reps -->
<div class="flex flex-col items-center">
<span class="font-headline-lg text-headline-lg text-on-surface leading-none">8</span>
<span class="font-label-sm text-label-sm text-on-surface-variant uppercase text-[10px]">Reps</span>
</div>
<!-- Plus Button -->
<button aria-label="Increase reps" class="w-12 h-12 flex items-center justify-center rounded-full bg-surface-variant text-on-surface active:scale-90 transition-transform hover:bg-surface-container-high">
<span class="material-symbols-outlined text-[24px]">add</span>
</button>
</div>
<!-- Bottom Action: Next Set / Rest -->
<button class="mt-4 mb-2 bg-primary-container text-on-primary-container font-label-sm text-label-sm uppercase rounded-full px-6 py-3 flex items-center justify-center space-x-2 active:scale-95 transition-transform w-full max-w-[180px] shadow-lg shadow-primary-container/20">
<span class="material-symbols-outlined text-[18px]">timer</span>
<span>Rest 90s</span>
</button>
<!-- Very bottom subtle indicator (e.g., Set 3 of 5) -->
<div class="absolute bottom-4 flex space-x-1">
<div class="w-1.5 h-1.5 rounded-full bg-primary-container"></div>
<div class="w-1.5 h-1.5 rounded-full bg-primary-container"></div>
<div class="w-1.5 h-1.5 rounded-full bg-primary-container scale-150"></div>
<div class="w-1.5 h-1.5 rounded-full bg-surface-variant"></div>
<div class="w-1.5 h-1.5 rounded-full bg-surface-variant"></div>
</div>
</main>
</body></html>
