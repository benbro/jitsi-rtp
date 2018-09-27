package org.jitsi.rtp.rtcp.rtcpfb

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import org.jitsi.rtp.extensions.toHex
import org.junit.jupiter.api.Assertions.*
import java.nio.ByteBuffer

internal class TccTest : ShouldSpec() {
    private val fci = ByteBuffer.wrap(byteArrayOf(
        // base=4, pkt status count=0x1729=5929
        0x00.toByte(), 0x04.toByte(), 0x17.toByte(), 0x29.toByte(),
        // ref time=0x298710, fbPktCount=1
        0x29.toByte(), 0x87.toByte(), 0x10.toByte(), 0x01.toByte(),

        // Chunks:
        // vector, 1-bit symbols, 1xR + 13xNR, 14 pkts (1 received)
        0xa0.toByte(), 0x00.toByte(),
        // vector, 1-bit symbols, 1xR + 13xNR, 14 pkts (1 received)
        0xa0.toByte(), 0x00.toByte(),
        // RLE, not received: 5886
        0x16.toByte(), 0xfe.toByte(),
        // vector, 2-bit symbols, 1x large delta + 6x small delta, 7 packets
        // (7 received)
        0xe5.toByte(), 0x55.toByte(),
        // vector, 1-bit symbols, 3xR + 2NR + 1R + 1NR + 1R [packets over, 6 remaining 0 bits]
        // (5 received)
        0xb9.toByte(), 0x40.toByte(),

        // deltas: Sx2, L, Sx11 (15 bytes)
        0x2c.toByte(), 0x78.toByte(),
        // the large one
        0xff.toByte(), 0x64.toByte(),
        0x04.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x04.toByte(), 0x00.toByte(), 0x04.toByte(), 0x04.toByte(),
        0x00.toByte(), 0x1c.toByte(), 0x34.toByte()
    ))

    private val fciAll2BitVectorChunks = ByteBuffer.wrap(byteArrayOf(
        // base=4, pkt status count=0x1E=30
        0x00.toByte(), 0x04.toByte(), 0x00.toByte(), 0x1E.toByte(),
        // ref time=0x298710, fbPktCount=1
        0x29.toByte(), 0x87.toByte(), 0x10.toByte(), 0x01.toByte(),

        // Chunks:
        // vector, 2-bit symbols, 1x large delta + 6x small delta, 7 packets
        // (7 received)
        0xe5.toByte(), 0x55.toByte(),
        // vector, 2-bit symbols, 1x large delta + 6x small delta, 7 packets
        // (7 received)
        0xe5.toByte(), 0x55.toByte(),
        // vector, 2-bit symbols, 7x not received
        // (0 received)
        0xc0.toByte(), 0x00.toByte(),
        // vector, 2-bit symbols, 7x not received
        // (0 received)
        0xc0.toByte(), 0x00.toByte(),
        // vector, 2-bit symbols, 1x large delta + 1x small delta, 2 packets
        // (2 received)
        0xe4.toByte(), 0x00.toByte(),

        // Deltas
        // 4: large (8000 ms)
        0x7d.toByte(), 0x00.toByte(),
        // 6x small
        // 5: 1, 6: 1, 7: 0, 8: 0, 9: 1, 10: 0
        0x04.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(), 0x04.toByte(), 0x00.toByte(),

        // 11: large (8000 ms)
        0x7d.toByte(), 0x00.toByte(),
        // 6x small
        // 12: 1, 13: 1, 14: 0, 15: 0, 16: 1, 17: 0
        0x04.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(), 0x04.toByte(), 0x00.toByte(),
        // 18-31 not received
        // 32: large (8000 ms)
        0x7d.toByte(), 0x00.toByte(),
        // 1x small
        // 33: 1
        0x04.toByte(),
        // Padding
        0x00.toByte(), 0x00.toByte(), 0x00.toByte()
    ))

    private val pktFromCall = ByteBuffer.wrap(byteArrayOf(
        0x00.toByte(), 0x01.toByte(), 0x00.toByte(), 0x7A.toByte(),
        0x9D.toByte(), 0xFB.toByte(), 0xF0.toByte(), 0x00.toByte(),
        0x20.toByte(), 0x7A.toByte(), 0x70.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x04.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x04.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x08.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
    ))

    init {
        "TCC Packet" {
            "Parsing a TCC packet from a buffer" {
                val tcc = Tcc(fci)
                should("parse the values correctly") {
                    // Based on the values in the packet above
                    tcc.feedbackPacketCount shouldBe 1
                    tcc.packetInfo.firstKey() shouldBe 4
                    // 5929 total packet statuses
                    tcc.packetInfo.size shouldBe 5929
                    // We should have 14 deltas
                    tcc.packetInfo.filter { it.value != NOT_RECEIVED_TS }.size shouldBe 14
                }
                "buf 2" { // with one bit and two bit symbols
                    val b = ByteBuffer.wrap(byteArrayOf(
                        0x15.toByte(), 0x60.toByte(), 0x00.toByte(), 0x0F.toByte(),
                        0x14.toByte(), 0x36.toByte(), 0xBD.toByte(), 0x2B.toByte(),
                        0x8A.toByte(), 0x50.toByte(), 0x40.toByte(), 0x01.toByte(),
                        0xFC.toByte(), 0x74.toByte(), 0x84.toByte(), 0x74.toByte(),
                        0x01.toByte(), 0x30.toByte(), 0x00.toByte(), 0x00.toByte()
                    ))
                    val t = Tcc(b)
//                    println(t)
                    t.getBuffer()
                }
                "buf 3" { // has a negative delta
                    val b = ByteBuffer.wrap(byteArrayOf(
                        0x00.toByte(), 0x0C.toByte(), 0x00.toByte(), 0xEC.toByte(),
                        0x15.toByte(), 0xF8.toByte(), 0xF7.toByte(), 0x00.toByte(),
                        0xBF.toByte(), 0xFE.toByte(), 0xA5.toByte(), 0x29.toByte(),
                        0x92.toByte(), 0x4A.toByte(), 0x94.toByte(), 0xF5.toByte(),
                        0xAC.toByte(), 0xCE.toByte(), 0xB3.toByte(), 0x33.toByte(),
                        0xAC.toByte(), 0xDA.toByte(), 0xB6.toByte(), 0x9B.toByte(),
                        0xAD.toByte(), 0x73.toByte(), 0xA7.toByte(), 0x66.toByte(),
                        0xB5.toByte(), 0xCE.toByte(), 0x9D.toByte(), 0xCE.toByte(),
                        0xB6.toByte(), 0xED.toByte(), 0x8D.toByte(), 0xCF.toByte(),
                        0x9C.toByte(), 0xED.toByte(), 0xAE.toByte(), 0x73.toByte(),
                        0xD1.toByte(), 0x19.toByte(), 0xD4.toByte(), 0x50.toByte(),
                        0xCC.toByte(), 0x00.toByte(), 0x00.toByte(), 0x04.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x04.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x04.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x04.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                        0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x04.toByte(),
                        0xFF.toByte(), 0xFC.toByte(), 0x2C.toByte(), 0x04.toByte(),
                        0x00.toByte(), 0x18.toByte(), 0x00.toByte(), 0x00.toByte()
                    ))
                    val t = Tcc(b)
//                    println(t)
                    t.getBuffer()
                }
            }
            "Creating a TCC packet" {
                val tcc = Tcc(fciAll2BitVectorChunks)
                val buf = tcc.getBuffer()
                should("write the data to the buffer correctly") {
                    buf.compareTo(fciAll2BitVectorChunks) shouldBe 0
                }
            }
            "Creating a TCC packet from values" {
                "which include a delta value on the border of the symbol size (64ms)" {
                    val packetInfo = PacketMap()
                    packetInfo[2585] = 1537916094447
                    packetInfo[2586] = 1537916094452
                    packetInfo[2587] = 1537916094475
                    packetInfo[2588] = 1537916094475
                    packetInfo[2589] = 1537916094481
                    packetInfo[2590] = 1537916094481
                    packetInfo[2591] = 1537916094486
                    packetInfo[2592] = 1537916094504
                    packetInfo[2593] = 1537916094504
                    packetInfo[2594] = 1537916094509
                    packetInfo[2595] = 1537916094509
                    packetInfo[2596] = 1537916094515
                    packetInfo[2597] = 1537916094536
                    packetInfo[2598] = 1537916094536
                    packetInfo[2599] = 1537916094542
                    packetInfo[2600] = 1537916094543
                    packetInfo[2601] = 1537916094607
                    packetInfo[2602] = 1537916094607
                    packetInfo[2603] = 1537916094613
                    packetInfo[2604] = 1537916094614
                    val tcc = Tcc(
                        referenceTime = 1537916094447,
                        feedbackPacketCount = 136,
                        packetInfo = packetInfo
                    )
                    should("serialize correctly") {
                        tcc.getBuffer()
                    }
                }
            }
            "values 2" {
                val packetMap = PacketMap()
                mapOf<Int, Long>(
                    1 to 3784062, 2 to 3784056, 3 to 3784056, 4 to 3784056, 5 to 3784056, 6 to 3784056, 7 to 3784056, 8 to 3784056, 9 to 3784056, 10 to 3784056, 11 to 3784056, 12 to 3784056, 13 to 3784056, 14 to 3784056, 15 to 3784056, 16 to 3784056, 17 to 3784056, 18 to 3784056, 19 to 3784056, 20 to 3784056, 21 to 3784056, 22 to 3784056, 23 to 3784056, 24 to 3784056, 25 to 3784056, 26 to 3784056, 27 to 3784056, 28 to 3784056, 29 to 3784056, 30 to 3784056, 31 to 3784056, 32 to 3784056, 33 to 3784056, 34 to 3784056, 35 to 3784056, 36 to 3784056, 37 to 3784056, 38 to 3784056, 39 to 3784056, 40 to 3784056, 41 to 3784056, 42 to 3784056, 43 to 3784056, 44 to 3784056, 45 to 3784056, 46 to 3784056, 47 to 3784056, 48 to 3784056, 49 to 3784056, 50 to 3784056, 51 to 3784056, 52 to 3784056, 53 to 3784056, 54 to 3784056, 55 to 3784056, 56 to 3784056, 57 to 3784056, 58 to 3784056, 59 to 3784056, 60 to 3784056, 61 to 3784056, 62 to 3784056, 63 to 3784056, 64 to 3784056, 65 to 3784056, 66 to 3784056, 67 to 3784056, 68 to 3784056, 69 to 3784056, 70 to 3784056, 71 to 3784056, 72 to 3784056, 73 to 3784056, 74 to -1, 75 to -1, 76 to -1, 77 to -1, 78 to -1, 79 to -1, 80 to -1, 81 to -1, 82 to -1, 83 to -1, 84 to -1, 85 to -1, 86 to -1, 87 to -1, 88 to -1, 89 to -1, 90 to -1, 91 to -1, 92 to -1, 93 to -1, 94 to -1, 95 to -1, 96 to -1, 97 to -1, 98 to -1, 99 to -1, 100 to -1, 101 to -1, 102 to -1, 103 to -1, 104 to -1, 105 to -1, 106 to -1, 107 to -1, 108 to -1, 109 to -1, 110 to -1, 111 to -1, 112 to -1
                ).toMap(packetMap)
                val tcc = Tcc(referenceTime = 3784056, packetInfo = packetMap)
                tcc.getBuffer()
            }
        }
        "PacketStatusChunk" {
            "parsing a vector chunk with 1 bit symbols" {
                // vector, 1-bit symbols, 1xR + 13xNR, 14 pkts (1 received)
                val chunkData = ByteBuffer.wrap(byteArrayOf(0xa0.toByte(), 0x00.toByte()))
                val chunk = PacketStatusChunk.parse(chunkData)
                should("parse the values correctly") {
                    chunk.getChunkType() shouldBe PacketStatusChunkType.STATUS_VECTOR_CHUNK
                    chunk.numPacketStatuses() shouldBe 14
                    chunk.forEachIndexed { index, status ->
                        when (index) {
                            0 -> status shouldBe OneBitPacketStatusSymbol.RECEIVED
                            in 1..13 -> status shouldBe OneBitPacketStatusSymbol.NOT_RECEIVED
                            else -> fail("Unexpected packet status, index: $index")
                        }
                    }
                }
            }
            "parsing a vector chunk with 2 bit symbols" {
                // vector, 2-bit symbols, 1x large delta + 6x small delta, 7 packets
                // (7 received)
                val chunkData = ByteBuffer.wrap(byteArrayOf(0xe5.toByte(), 0x55.toByte()))
                val chunk = PacketStatusChunk.parse(chunkData)
                should("parse the values correctly") {
                    chunk.getChunkType() shouldBe PacketStatusChunkType.STATUS_VECTOR_CHUNK
                    chunk.numPacketStatuses() shouldBe 7
                    chunk.forEachIndexed { index, status ->
                        when (index) {
                            0 -> status shouldBe TwoBitPacketStatusSymbol.RECEIVED_LARGE_OR_NEGATIVE_DELTA
                            in 1..6 -> status shouldBe TwoBitPacketStatusSymbol.RECEIVED_SMALL_DELTA
                            else -> fail("Unexpected packet status, index: $index")
                        }
                    }
                }
            }
            "parsing a vector chunk with 1 bit symbols and 'extra' statuses" {
                // vector, 1-bit symbols, 3xR + 2NR + 1R + 1NR + 1R [packets over, 6 remaining 0 bits] (5 received)
                val chunkData = ByteBuffer.wrap(byteArrayOf(0xb9.toByte(), 0x40.toByte()))
                val chunk = PacketStatusChunk.parse(chunkData)
                should("parse the values correctly") {
                    chunk.getChunkType() shouldBe PacketStatusChunkType.STATUS_VECTOR_CHUNK
                    // Even though there are 'extra' statuses in the last few bits, the PacketStatusChunk
                    // doesn't know/care about that, so it should parse all of them
                    chunk.numPacketStatuses() shouldBe 14
                    chunk.forEachIndexed { index, status ->
                        when (index) {
                            in 0..2 -> status shouldBe OneBitPacketStatusSymbol.RECEIVED
                            3, 4 -> status shouldBe OneBitPacketStatusSymbol.NOT_RECEIVED
                            5 -> status shouldBe OneBitPacketStatusSymbol.RECEIVED
                            6 -> status shouldBe OneBitPacketStatusSymbol.NOT_RECEIVED
                            7 -> status shouldBe OneBitPacketStatusSymbol.RECEIVED
                            in 8..14 -> Unit
                            else -> fail("Unexpected packet status, index: $index")
                        }
                    }
                }
            }
            "parsing a run length chunk" {
                val chunkData = ByteBuffer.wrap(byteArrayOf(0x16.toByte(), 0xfe.toByte()))
                // RLE, not received: 5886
                val chunk = PacketStatusChunk.parse(chunkData)
                should("parse the values correctly") {
                    chunk.getChunkType() shouldBe PacketStatusChunkType.RUN_LENGTH_CHUNK
                    chunk.numPacketStatuses() shouldBe 5886
                    chunk.forEach { status ->
                        status shouldBe TwoBitPacketStatusSymbol.NOT_RECEIVED
                    }
                }
            }
        }
        "ReceiveDelta" {
            "EightBitReceiveDelta" {

            }
        }
    }
}
