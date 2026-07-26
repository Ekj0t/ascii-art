# ASCII Art Generator

A lightweight Java application that transforms images into beautiful ASCII art using customizable character sets and image processing techniques. Simply upload an image, tweak the settings, and generate text-based artwork in seconds.

---

## Features

- Convert images into ASCII art
- Support for common image formats (JPG, PNG, etc.)
- Live preview of uploaded images
- Adjustable output width and scaling
- Brightness and contrast controls
- Invert colors option
- Multiple ASCII character sets
- Save generated ASCII art as a text file
- Copy ASCII art directly to the clipboard
- Fast and lightweight Java application

---

## Why ASCII Art?

ASCII art is a fun and creative way to represent images using text characters. It has applications in:

- Terminal graphics
- Retro computing
- Programming projects
- Social media posts
- Digital art
- Learning image processing concepts

---

## Built With

- Java
- JavaFX
- Maven

---

## Installation

Clone the repository:

```bash
git clone https://github.com/Ekj0t/ascii-art.git
```

Navigate into the project directory:

```bash
cd ascii-art
```

Build the project:

```bash
mvn clean install
```

Run the application:

```bash
mvn javafx:run
```

Alternatively, open the project in IntelliJ IDEA or your preferred Java IDE and run the main application.

---

## How It Works

1. Upload an image.
2. Resize the image based on the selected output width.
3. Convert the image to grayscale.
4. Map each pixel's brightness to a corresponding ASCII character.
5. Display the generated ASCII art.
6. Copy or save the output.

---

## Project Structure

```text
ascii-art/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
├── pom.xml
├── .gitignore
└── README.md
```

---

## Roadmap

Planned improvements include:

- [ ] Colour ASCII art
- [ ] GIF to ASCII conversion
- [ ] Video to ASCII conversion
- [ ] Real-time webcam ASCII
- [ ] Custom font support
