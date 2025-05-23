/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2025 miaixz.org OSHI and other contributors.               ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
package org.miaixz.bus.health.unix.jna;

import java.nio.ByteBuffer;

import org.miaixz.bus.health.Builder;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

/**
 * C library. This class should be considered non-API as it may be removed if/when its code is incorporated into the JNA
 * project.
 */
public interface SolarisLibc extends CLibrary {

    SolarisLibc INSTANCE = Native.load("c", SolarisLibc.class);

    int UTX_USERSIZE = 32;
    int UTX_LINESIZE = 32;
    int UTX_IDSIZE = 4;
    int UTX_HOSTSIZE = 257;

    int PRCLSZ = 8;
    int PRFNSZ = 16;
    int PRLNSZ = 32;
    int PRARGSZ = 80;

    /**
     * Reads a line from the current file position in the utmp file. It returns a pointer to a structure containing the
     * fields of the line.
     * <p>
     * Not thread safe
     *
     * @return a {@link SolarisUtmpx} on success, and NULL on failure (which includes the "record not found" case)
     */
    SolarisUtmpx getutxent();

    /**
     * Returns the thread ID of the calling thread.
     *
     * @return the thread ID of the calling thread.
     */
    int thr_self();

    /**
     * Connection info
     */
    @FieldOrder({ "ut_user", "ut_id", "ut_line", "ut_pid", "ut_type", "ut_exit", "ut_tv", "ut_session", "pad",
            "ut_syslen", "ut_host" })
    class SolarisUtmpx extends Structure {
        public byte[] ut_user = new byte[UTX_USERSIZE]; // user login name
        public byte[] ut_id = new byte[UTX_IDSIZE]; // etc/inittab id (usually line #)
        public byte[] ut_line = new byte[UTX_LINESIZE]; // device name
        public int ut_pid; // process id
        public short ut_type; // type of entry
        public Exit_status ut_exit; // process termination/exit status
        public Timeval ut_tv; // time entry was made
        public int ut_session; // session ID, used for windowing
        public int[] pad = new int[5]; // reserved for future use
        public short ut_syslen; // significant length of ut_host including terminating null
        public byte[] ut_host = new byte[UTX_HOSTSIZE]; // host name
    }

    /**
     * Part of utmpx structure
     */
    @FieldOrder({ "e_termination", "e_exit" })
    class Exit_status extends Structure {
        public short e_termination; // Process termination status
        public short e_exit; // Process exit status
    }

    /**
     * 32/64-bit timeval required for utmpx structure
     */
    @FieldOrder({ "tv_sec", "tv_usec" })
    class Timeval extends Structure {
        public NativeLong tv_sec; // seconds
        public NativeLong tv_usec; // microseconds
    }

    /**
     * Structure for psinfo file
     */
    class SolarisPsInfo {
        public int pr_flag; // process flags (DEPRECATED; do not use)
        public int pr_nlwp; // number of active lwps in the process
        public int pr_pid; // unique process id
        public int pr_ppid; // process id of parent
        public int pr_pgid; // pid of process group leader
        public int pr_sid; // session id
        public int pr_uid; // real user id
        public int pr_euid; // effective user id
        public int pr_gid; // real group id
        public int pr_egid; // effective group id
        public Pointer pr_addr; // address of process
        public size_t pr_size; // size of process image in Kbytes
        public size_t pr_rssize; // resident set size in Kbytes
        public size_t pr_rssizepriv; // resident set size of private mappings
        public NativeLong pr_ttydev; // controlling tty device (or PRNODEV)
        // The following percent numbers are 16-bit binary
        // fractions [0 .. 1] with the binary point to the
        // right of the high-order bit (1.0 == 0x8000)
        public short pr_pctcpu; // % of recent cpu time used by all lwps
        public short pr_pctmem; // % of system memory used by process
        public Timestruc pr_start; // process start time, from the epoch
        public Timestruc pr_time; // usr+sys cpu time for this process
        public Timestruc pr_ctime; // usr+sys cpu time for reaped children
        public byte[] pr_fname = new byte[PRFNSZ]; // name of exec'ed file
        public byte[] pr_psargs = new byte[PRARGSZ]; // initial characters of arg list
        public int pr_wstat; // if zombie, the wait() status
        public int pr_argc; // initial argument count
        public Pointer pr_argv; // address of initial argument vector
        public Pointer pr_envp; // address of initial environment vector
        public byte pr_dmodel; // data model of the process
        public byte[] pr_pad2 = new byte[3];
        public int pr_taskid; // task id
        public int pr_projid; // project id
        public int pr_nzomb; // number of zombie lwps in the process
        public int pr_poolid; // pool id
        public int pr_zoneid; // zone id
        public int pr_contract; // process contract id
        public int pr_filler; // 4 bytes reserved for future use
        public SolarisLwpsInfo pr_lwp; // information for representative lwp

        public SolarisPsInfo(ByteBuffer buff) {
            this.pr_flag = Builder.readIntFromBuffer(buff);
            this.pr_nlwp = Builder.readIntFromBuffer(buff);
            this.pr_pid = Builder.readIntFromBuffer(buff);
            this.pr_ppid = Builder.readIntFromBuffer(buff);
            this.pr_pgid = Builder.readIntFromBuffer(buff);
            this.pr_sid = Builder.readIntFromBuffer(buff);
            this.pr_uid = Builder.readIntFromBuffer(buff);
            this.pr_euid = Builder.readIntFromBuffer(buff);
            this.pr_gid = Builder.readIntFromBuffer(buff);
            this.pr_egid = Builder.readIntFromBuffer(buff);
            this.pr_addr = Builder.readPointerFromBuffer(buff);
            this.pr_size = Builder.readSizeTFromBuffer(buff);
            this.pr_rssize = Builder.readSizeTFromBuffer(buff);
            this.pr_rssizepriv = Builder.readSizeTFromBuffer(buff);
            this.pr_ttydev = Builder.readNativeLongFromBuffer(buff);
            this.pr_pctcpu = Builder.readShortFromBuffer(buff);
            this.pr_pctmem = Builder.readShortFromBuffer(buff);
            // Force 8 byte alignment
            if (Native.LONG_SIZE > 4) {
                Builder.readIntFromBuffer(buff);
            }
            this.pr_start = new Timestruc(buff);
            this.pr_time = new Timestruc(buff);
            this.pr_ctime = new Timestruc(buff);
            Builder.readByteArrayFromBuffer(buff, this.pr_fname);
            Builder.readByteArrayFromBuffer(buff, this.pr_psargs);
            this.pr_wstat = Builder.readIntFromBuffer(buff);
            this.pr_argc = Builder.readIntFromBuffer(buff);
            this.pr_argv = Builder.readPointerFromBuffer(buff);
            this.pr_envp = Builder.readPointerFromBuffer(buff);
            this.pr_dmodel = Builder.readByteFromBuffer(buff);
            Builder.readByteArrayFromBuffer(buff, this.pr_pad2);
            this.pr_taskid = Builder.readIntFromBuffer(buff);
            this.pr_projid = Builder.readIntFromBuffer(buff);
            this.pr_nzomb = Builder.readIntFromBuffer(buff);
            this.pr_poolid = Builder.readIntFromBuffer(buff);
            this.pr_zoneid = Builder.readIntFromBuffer(buff);
            this.pr_contract = Builder.readIntFromBuffer(buff);
            this.pr_filler = Builder.readIntFromBuffer(buff);
            this.pr_lwp = new SolarisLwpsInfo(buff);
        }
    }

    /**
     * Nested Structure for psinfo file
     */
    class SolarisLwpsInfo {
        public int pr_flag; // lwp flags (DEPRECATED; do not use)
        public int pr_lwpid; // lwp id
        public Pointer pr_addr; // DEPRECATED was internal address of lwp
        public Pointer pr_wchan; // DEPRECATED was wait addr for sleeping lwp
        public byte pr_stype; // synchronization event type
        public byte pr_state; // numeric lwp state
        public byte pr_sname; // printable character for pr_state
        public byte pr_nice; // nice for cpu usage
        public short pr_syscall; // system call number (if in syscall)
        public byte pr_oldpri; // pre-SVR4, low value is high priority
        public byte pr_cpu; // pre-SVR4, cpu usage for scheduling
        public int pr_pri; // priority, high value = high priority
        // The following percent numbers are 16-bit binary
        // fractions [0 .. 1] with the binary point to the
        // right of the high-order bit (1.0 == 0x8000)
        public short pr_pctcpu; // % of recent cpu time used by this lwp
        public short pr_pad;
        public Timestruc pr_start; // lwp start time, from the epoch
        public Timestruc pr_time; // cpu time for this lwp
        public byte[] pr_clname = new byte[PRCLSZ]; // scheduling class name
        public byte[] pr_oldname = new byte[PRFNSZ]; // binary compatibility -- unused
        public int pr_onpro; // processor which last ran this lwp
        public int pr_bindpro; // processor to which lwp is bound
        public int pr_bindpset; // processor set to which lwp is bound
        public int pr_lgrp; // home lgroup
        public long pr_last_onproc; // Timestamp of when thread last ran on a processor
        public byte[] pr_name = new byte[PRLNSZ]; // name of system lwp

        public SolarisLwpsInfo(ByteBuffer buff) {
            this.pr_flag = Builder.readIntFromBuffer(buff);
            this.pr_lwpid = Builder.readIntFromBuffer(buff);
            this.pr_addr = Builder.readPointerFromBuffer(buff);
            this.pr_wchan = Builder.readPointerFromBuffer(buff);
            this.pr_stype = Builder.readByteFromBuffer(buff);
            this.pr_state = Builder.readByteFromBuffer(buff);
            this.pr_sname = Builder.readByteFromBuffer(buff);
            this.pr_nice = Builder.readByteFromBuffer(buff);
            this.pr_syscall = Builder.readShortFromBuffer(buff);
            this.pr_oldpri = Builder.readByteFromBuffer(buff);
            this.pr_cpu = Builder.readByteFromBuffer(buff);
            this.pr_pri = Builder.readIntFromBuffer(buff);
            this.pr_pctcpu = Builder.readShortFromBuffer(buff);
            this.pr_pad = Builder.readShortFromBuffer(buff);
            this.pr_start = new Timestruc(buff);
            this.pr_time = new Timestruc(buff);
            Builder.readByteArrayFromBuffer(buff, this.pr_clname);
            Builder.readByteArrayFromBuffer(buff, this.pr_oldname);
            this.pr_onpro = Builder.readIntFromBuffer(buff);
            this.pr_bindpro = Builder.readIntFromBuffer(buff);
            this.pr_bindpset = Builder.readIntFromBuffer(buff);
            this.pr_lgrp = Builder.readIntFromBuffer(buff);
            this.pr_last_onproc = Builder.readLongFromBuffer(buff);
            Builder.readByteArrayFromBuffer(buff, this.pr_name);
        }
    }

    /**
     * Structure for usage file
     */
    class SolarisPrUsage {
        public int pr_lwpid; // lwp id. 0: process or defunct
        public int pr_count; // number of contributing lwps
        public Timestruc pr_tstamp; // current time stamp
        public Timestruc pr_create; // process/lwp creation time stamp
        public Timestruc pr_term; // process/lwp termination time stamp
        public Timestruc pr_rtime; // total lwp real (elapsed) time
        public Timestruc pr_utime; // user level cpu time
        public Timestruc pr_stime; // system call cpu time
        public Timestruc pr_ttime; // other system trap cpu time
        public Timestruc pr_tftime; // text page fault sleep time
        public Timestruc pr_dftime; // data page fault sleep time
        public Timestruc pr_kftime; // kernel page fault sleep time
        public Timestruc pr_ltime; // user lock wait sleep time
        public Timestruc pr_slptime; // all other sleep time
        public Timestruc pr_wtime; // wait-cpu (latency) time
        public Timestruc pr_stoptime; // stopped time
        public Timestruc[] filltime = new Timestruc[6]; // filler for future expansion
        public NativeLong pr_minf; // minor page faults
        public NativeLong pr_majf; // major page faults
        public NativeLong pr_nswap; // swaps
        public NativeLong pr_inblk; // input blocks
        public NativeLong pr_oublk; // output blocks
        public NativeLong pr_msnd; // messages sent
        public NativeLong pr_mrcv; // messages received
        public NativeLong pr_sigs; // signals received
        public NativeLong pr_vctx; // voluntary context switches
        public NativeLong pr_ictx; // involuntary context switches
        public NativeLong pr_sysc; // system calls
        public NativeLong pr_ioch; // chars read and written
        public NativeLong[] filler = new NativeLong[10]; // filler for future expansion

        public SolarisPrUsage(ByteBuffer buff) {
            this.pr_lwpid = Builder.readIntFromBuffer(buff);
            this.pr_count = Builder.readIntFromBuffer(buff);
            this.pr_tstamp = new Timestruc(buff);
            this.pr_create = new Timestruc(buff);
            this.pr_term = new Timestruc(buff);
            this.pr_rtime = new Timestruc(buff);
            this.pr_utime = new Timestruc(buff);
            this.pr_stime = new Timestruc(buff);
            this.pr_ttime = new Timestruc(buff);
            this.pr_tftime = new Timestruc(buff);
            this.pr_dftime = new Timestruc(buff);
            this.pr_kftime = new Timestruc(buff);
            this.pr_ltime = new Timestruc(buff);
            this.pr_slptime = new Timestruc(buff);
            this.pr_wtime = new Timestruc(buff);
            this.pr_stoptime = new Timestruc(buff);
            for (int i = 0; i < filltime.length; i++) {
                this.filltime[i] = new Timestruc(buff);
            }
            this.pr_minf = Builder.readNativeLongFromBuffer(buff);
            this.pr_majf = Builder.readNativeLongFromBuffer(buff);
            this.pr_nswap = Builder.readNativeLongFromBuffer(buff);
            this.pr_inblk = Builder.readNativeLongFromBuffer(buff);
            this.pr_oublk = Builder.readNativeLongFromBuffer(buff);
            this.pr_msnd = Builder.readNativeLongFromBuffer(buff);
            this.pr_mrcv = Builder.readNativeLongFromBuffer(buff);
            this.pr_sigs = Builder.readNativeLongFromBuffer(buff);
            this.pr_vctx = Builder.readNativeLongFromBuffer(buff);
            this.pr_ictx = Builder.readNativeLongFromBuffer(buff);
            this.pr_sysc = Builder.readNativeLongFromBuffer(buff);
            this.pr_ioch = Builder.readNativeLongFromBuffer(buff);
            for (int i = 0; i < filler.length; i++) {
                this.filler[i] = Builder.readNativeLongFromBuffer(buff);
            }
        }
    }

    /**
     * 32/64-bit timestruc required for psinfo and lwpsinfo structures
     */
    class Timestruc {
        public NativeLong tv_sec; // seconds
        public NativeLong tv_nsec; // nanoseconds

        public Timestruc(ByteBuffer buff) {
            this.tv_sec = Builder.readNativeLongFromBuffer(buff);
            this.tv_nsec = Builder.readNativeLongFromBuffer(buff);
        }
    }

}
