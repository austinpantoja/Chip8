package pantoja.chip8.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileIO {

    private FileIO() {
    }


    /**
     * Uses the file channel API to reads a file and return its contents as a byte array
     *
     * @param path The file path to be read
     * @return A byte array containing the contents of the file
     * @throws IOException Thrown when I/O fails
     */
    public static byte[] readAllBytes(String path) throws IOException {
        try (FileChannel channel = FileChannel.open(Path.of(path))) {
            ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
            channel.read(buffer);
            return buffer.array();
        }
    }


    /**
     * Uses the file channel API to read a file into a provided byte array starting at a specified offset
     *
     * @param path        The file path to be read
     * @param destination An already allocated byte array to read the into
     * @param offset      The index to begin writing the file to
     * @throws IOException Thrown when I/O fails
     */
    public static void readIntoBuffer(
            String path,
            byte[] destination,
            int offset
    ) throws IOException {

        if (offset < 0 || offset >= destination.length) {
            throw new IndexOutOfBoundsException("Invalid offset: " + offset + " while reading " + path);
        }

        try (FileChannel channel = FileChannel.open(Path.of(path))) {
            long fileSize = channel.size();
            int maxWritable = destination.length - offset;

            // Throw and exception if there is not enough allocated memory in destination array
            if (fileSize > maxWritable) {
                throw new IOException(
                        "File too large: " + fileSize +
                                " bytes; available space from offset " + offset +
                                " is " + maxWritable + " bytes (" + path + ")"
                );
            }

            ByteBuffer buffer = ByteBuffer.wrap(destination, offset, (int) fileSize);

            while (buffer.hasRemaining()) {
                int read = channel.read(buffer);
                if (read == -1) {
                    break;
                }
            }
        }
    }


    /**
     * Writes the given byte array to a file using FileChannel.
     * Creates parent directories if needed and truncates existing files.
     *
     * @param data bytes to write
     * @param path destination file path
     * @throws IOException if writing fails
     */
    public static void writeBytes(byte[] data, String path) throws IOException {
        Path p = Path.of(path);
        Path parent = p.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (FileChannel channel = FileChannel.open(
                p,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        )) {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
        }
    }
}
